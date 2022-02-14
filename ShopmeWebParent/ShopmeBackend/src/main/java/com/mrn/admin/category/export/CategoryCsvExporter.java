package com.mrn.admin.category.export;

import com.mrn.admin.AbstractExporter;
import com.mrn.common.entity.Category;
import com.mrn.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {

    public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Category ID", "Category Name" };
        String[] fieldMapping = {"id", "name" };

        csvBeanWriter.writeHeader(csvHeader);

        // write on file the user on the filed mapping
        for (Category category : listCategories) {
            category.setName(category.getName().replaceAll("--", "  "));
            csvBeanWriter.write(category, fieldMapping);
        }

        csvBeanWriter.close();
    }
}
