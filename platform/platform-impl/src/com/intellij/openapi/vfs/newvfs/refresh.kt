// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.vfs.newvfs

import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.blockingContext
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.io.awaitFor
import kotlinx.coroutines.*
import java.util.function.Supplier

@Service
internal class RefreshWorkerHelper {
  @JvmField
  val parallelism: Int = Registry.intValue("vfs.refresh.worker.parallelism", 6).coerceIn(1, Runtime.getRuntime().availableProcessors())

  @OptIn(ExperimentalCoroutinesApi::class)
  private val dispatcher = Dispatchers.IO.limitedParallelism(parallelism) + CoroutineName("VFS Refresh")

  fun parallelScan(events: MutableList<VFileEvent>, mapper: Supplier<List<VFileEvent>>) {
    @Suppress("RAW_RUN_BLOCKING")
    runBlocking {
      Array(parallelism) {
        async(dispatcher) {
          mapper.get()
        }
      }
    }.flatMapTo(events) {
      @Suppress("OPT_IN_USAGE")
      it.getCompleted()
    }
  }
}

suspend fun refreshVFSAsync() {
  val sessionId = blockingContext {
    VirtualFileManager.getInstance().asyncRefresh()
  }
  val refreshQueueImpl = RefreshQueue.getInstance() as? RefreshQueueImpl
  val session = refreshQueueImpl?.getSession(sessionId) ?: return
  try {
    session.semaphore.awaitFor()
  }
  catch (t: Throwable) {
    refreshQueueImpl.cancelSession(sessionId)
    throw t
  }
}
