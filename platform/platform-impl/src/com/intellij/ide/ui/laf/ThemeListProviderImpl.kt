// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.ui.laf

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.TargetUIType
import com.intellij.ide.ui.ThemeListProvider
import com.intellij.ui.ExperimentalUI

private class ThemeListProviderImpl : ThemeListProvider {
  override fun getShownThemes(): List<List<UIThemeLookAndFeelInfo>> {
    val lafManager = LafManager.getInstance() as? LafManagerImpl ?: return emptyList()
    val result = mutableListOf<List<UIThemeLookAndFeelInfo>>()
    if (ExperimentalUI.isNewUI()) {
      result.add(lafManager.getThemeListForTargetUI(TargetUIType.NEW).sortedBy { it.name }.toList())
    }
    result.add((lafManager.getThemeListForTargetUI(TargetUIType.CLASSIC).filterNot { it.theme.id == "IntelliJ" }
                + lafManager.getThemeListForTargetUI(TargetUIType.UNSPECIFIED)).sortedBy { it.name }.toList())
    return result
  }
}