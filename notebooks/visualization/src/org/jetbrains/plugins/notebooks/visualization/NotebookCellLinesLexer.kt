package org.jetbrains.plugins.notebooks.visualization

import com.intellij.lang.Language
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.tree.IElementType
import com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.plugins.notebooks.visualization.NotebookCellLines.CellType
import org.jetbrains.plugins.notebooks.visualization.NotebookCellLines.MarkersAtLines
import kotlin.math.max

interface NotebookCellLinesLexer {
  fun markerSequence(chars: CharSequence, ordinalIncrement: Int, offsetIncrement: Int, defaultLanguage: Language): Sequence<Marker>

  data class Marker(
    val ordinal: Int,
    val type: CellType,
    val offset: Int,
    val length: Int,
    val data: KeyFMap,
  ) : Comparable<Marker> {
    override fun compareTo(other: Marker): Int = offset - other.offset
  }

  companion object {
    fun defaultMarkerSequence(underlyingLexerFactory: () -> Lexer,
                              getCellTypeAndData: (IElementType, lexer: Lexer) -> Pair<CellType, KeyFMap>?,
                              chars: CharSequence,
                              ordinalIncrement: Int,
                              offsetIncrement: Int): Sequence<Marker> = sequence {
      val lexer = underlyingLexerFactory()
      lexer.start(chars, 0, chars.length)
      var ordinal = 0
      while (true) {
        val tokenType = lexer.tokenType ?: break
        getCellTypeAndData(tokenType, lexer)?.let { (type, data) ->
          yield(Marker(
            ordinal = ordinal++ + ordinalIncrement,
            type = type,
            offset = lexer.currentPosition.offset + offsetIncrement,
            length = lexer.tokenText.length,
            data = data,
          ))
        }
        lexer.advance()
      }
    }

    fun defaultIntervals(document: Document, markers: List<Marker>): List<NotebookCellLines.Interval> {
      val data = KeyFMap.EMPTY_MAP.plus(NotebookCellLines.INTERVAL_LANGUAGE_KEY, PlainTextLanguage.INSTANCE)
      val intervals = toIntervalsInfo(document, markers, firstMarkerData = data, lastMarkerData = data)

      val result = mutableListOf<NotebookCellLines.Interval>()
      for (i in 0 until (intervals.size - 1)) {
        result += NotebookCellLines.Interval(ordinal = i, type = intervals[i].cellType,
                                             lines = intervals[i].lineNumber until intervals[i + 1].lineNumber,
                                             markers = intervals[i].markersAtLInes,
                                             intervals[i].data)
      }
      return result
    }

  }
}

private data class IntervalInfo(val lineNumber: Int, val cellType: CellType, val markersAtLInes: MarkersAtLines, val data: KeyFMap)

private fun toIntervalsInfo(document: Document,
                            markers: List<NotebookCellLinesLexer.Marker>,
                            firstMarkerData: KeyFMap,
                            lastMarkerData: KeyFMap): List<IntervalInfo> {
  val m = mutableListOf<IntervalInfo>()

  // add first if necessary
  if (markers.isEmpty() || document.getLineNumber(markers.first().offset) != 0) {
    m += IntervalInfo(0, CellType.RAW, MarkersAtLines.NO, firstMarkerData)
  }

  for (marker in markers) {
    m += IntervalInfo(document.getLineNumber(marker.offset), marker.type, MarkersAtLines.TOP, marker.data)
  }

  // marker for the end
  m += IntervalInfo(max(document.lineCount, 1), CellType.RAW, MarkersAtLines.NO, lastMarkerData)
  return m
}