package ie.ncirl.container.manager.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageData {
    private List<Integer> pageNumbers;
    private Integer currPage;
    private Integer totalPages;
}
