// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.terminal.exp

import com.intellij.ui.JBColor
import java.awt.Color

@Suppress("ConstPropertyName")
object TerminalUi {
  const val blockTopInset = 8
  const val blockBottomInset = 12
  const val blockLeftInset = 12
  const val blockRightInset = 12
  const val cornerToBlockInset = 7
  const val blockArc = 8
  const val blocksGap = 6

  const val promptTopInset = 11
  const val promptBottomInset = 12
  const val promptToCommandInset = 2

  const val alternateBufferLeftInset = 4

  // todo: create color keys
  val terminalBackground: Color
    get() = JBColor(0xFFFFFF, 0x1E1F22)
  val blockBackgroundStart: Color
    get() = JBColor(0xF0F2F5, 0x2B2D30)
  val blockBackgroundEnd: Color
    get() = JBColor(0xFAFAFA, 0x212329)
  val selectedBlockBackground: Color
    get() = JBColor(0xEDF3FF, 0x25324D)
  val selectedBlockStrokeColor: Color
    get() = JBColor(0x4682FA, 0x3574F0)
  val errorBlockBackground: Color
    get() = JBColor(0xFFF2F3, 0x402929)
  val errorBlockStrokeColor: Color
    get() = JBColor(0xED99A1, 0x9C4E4E)
  val outputForeground: Color
    get() = JBColor(0x080808, 0xBDC0C9)
  val promptForeground: Color
    get() = JBColor(0x6C707E, 0x868A91)
}