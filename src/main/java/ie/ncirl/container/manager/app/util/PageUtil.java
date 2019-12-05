package ie.ncirl.container.manager.app.util;

import ie.ncirl.container.manager.app.dto.PageData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageUtil {

    /**
     * A helper method that can be used to fill a Spring model with Thymeleaf required pagination data
     *
     * @param model
     * @param page
     */
    public static PageData getPageData(Page page) {
        int totalPages = page.getTotalPages();
        int offset = 5;
        PageData pageData = new PageData();

        if (totalPages > 0) {
            int startPage = page.getNumber() - offset;
            startPage = startPage > 0 ? startPage : 1;
            int lastPage = page.getNumber() + offset;
            lastPage = lastPage <= totalPages ? lastPage : totalPages;
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, lastPage)
                    .boxed()
                    .collect(Collectors.toList());
            pageData.setPageNumbers(pageNumbers);
            pageData.setCurrPage(page.getNumber());
            if (lastPage < totalPages) {
                pageData.setTotalPages(totalPages);
            }
        }

        return pageData;
    }
}
