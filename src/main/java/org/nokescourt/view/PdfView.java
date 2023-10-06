package org.nokescourt.view;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nokescourt.model.Exam;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.view.AbstractView;

import java.util.List;
import java.util.Map;

public final class PdfView extends AbstractView {
    public PdfView() {
        setContentType(MediaType.APPLICATION_PDF_VALUE);
    }

    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model,
                                           @NonNull final HttpServletRequest request,
                                           @NonNull final HttpServletResponse response) throws Exception {

        try (final Document document = new Document(new PdfDocument(new PdfWriter(response.getOutputStream())), PageSize.A4)) {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            document.add(new Paragraph("Exams"));
            document.setFont(font);
            List<Exam> exams = (List<Exam>) model.get("exams");
            Table table = new Table(2);
            table.addCell(new Cell()
                    .add(new Paragraph("Course")
                            .setFont(font)
                            .setFontColor(ColorConstants.DARK_GRAY))
                    .setBackgroundColor(ColorConstants.CYAN, 0.2f)
                    .setBorder(Border.NO_BORDER)
                    .setBorderRight(new SolidBorder(1))
                    .setBorderBottom(new SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell()
                    .add(new Paragraph("Exam")
                            .setFont(font)
                            .setFontColor(ColorConstants.DARK_GRAY))
                    .setBackgroundColor(ColorConstants.PINK, 0.2f)
                    .setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            for (Exam exam : exams) {
                table.addCell(new Cell()
                        .add(new Paragraph(exam.course().code())
                                .setFont(font)
                                .setFontColor(ColorConstants.DARK_GRAY))
                        .setBackgroundColor(ColorConstants.CYAN, 0.2f)
                        .setBorder(Border.NO_BORDER)
                        .setBorderRight(new SolidBorder(1))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell()
                        .add(new Paragraph(exam.code())
                                .setFont(font)
                                .setFontColor(ColorConstants.DARK_GRAY))
                        .setBackgroundColor(ColorConstants.PINK, 0.2f)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.CENTER));
            }
            document.add(table);
        }
    }
}
