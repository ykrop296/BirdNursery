package com.example.birdnursery.ui.screens.pedigree

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.GouldianGenetics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Печать / выгрузка семейного дерева в PDF через системный диалог печати. */
object PedigreePrinter {

    fun print(
        context: Context,
        root: Bird,
        ancestors: List<PedigreeEntry>,
        descendants: List<PedigreeEntry>
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Дерево — ${root.name.ifBlank { "птица" }}"
        printManager.print(jobName, PedigreePrintAdapter(buildLines(root, ancestors, descendants)), null)
    }

    private fun line(bird: Bird, role: String?, indent: Int): String {
        val sex = if (bird.sex.isNotBlank()) bird.sex else "пол не указан"
        val phenotype = GouldianGenetics.phenotypeLabel(bird)
        val born = if (bird.birthDate.isNotBlank()) ", рожд. ${bird.birthDate}" else ""
        val prefix = if (role != null) "$role: " else ""
        return "    ".repeat(indent) + "• " + prefix + "${bird.name} ($sex) — $phenotype$born"
    }

    fun buildLines(
        root: Bird,
        ancestors: List<PedigreeEntry>,
        descendants: List<PedigreeEntry>
    ): List<String> {
        val lines = mutableListOf<String>()
        lines += "Питомник — семейное дерево"
        lines += "Выгружено: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        lines += ""
        lines += "Птица"
        lines += line(root, null, 1)
        lines += ""
        lines += "Предки"
        if (ancestors.isEmpty()) lines += "    • не указаны"
        else ancestors.forEach { lines += line(it.bird, it.role, it.depth) }
        lines += ""
        lines += "Потомки"
        if (descendants.isEmpty()) lines += "    • нет"
        else descendants.forEach { lines += line(it.bird, it.role, it.depth) }
        return lines
    }

    private class PedigreePrintAdapter(private val lines: List<String>) : PrintDocumentAdapter() {

        private var pdfDocument: PdfDocument? = null

        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback?,
            extras: Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onLayoutCancelled()
                return
            }
            val info = PrintDocumentInfo.Builder("pedigree.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()
            callback?.onLayoutFinished(info, true)
        }

        override fun onWrite(
            pages: Array<out PageRange>?,
            destination: ParcelFileDescriptor?,
            cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onWriteCancelled()
                return
            }
            val document = PdfDocument()
            pdfDocument = document

            val pageWidth = 595 // A4, pt
            val pageHeight = 842
            val margin = 48
            val lineHeight = 20f

            val titlePaint = Paint().apply {
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val textPaint = Paint().apply { textSize = 12f }

            var pageNumber = 1
            var page = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            )
            var y = margin.toFloat()

            fun newPage() {
                document.finishPage(page)
                pageNumber++
                page = document.startPage(
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                )
                y = margin.toFloat()
            }

            lines.forEachIndexed { index, text ->
                if (y > pageHeight - margin) newPage()
                val paint = if (index == 0) titlePaint else textPaint
                page.canvas.drawText(text, margin.toFloat(), y, paint)
                y += if (index == 0) lineHeight * 1.4f else lineHeight
            }
            document.finishPage(page)

            try {
                ParcelFileDescriptor.AutoCloseOutputStream(destination).use {
                    document.writeTo(it)
                }
                callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback?.onWriteFailed(e.message)
            } finally {
                document.close()
                pdfDocument = null
            }
        }

        override fun onFinish() {
            pdfDocument?.close()
            pdfDocument = null
        }
    }
}
