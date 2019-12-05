package ie.ncirl.container.manager.app.util;

import ie.ncirl.container.manager.app.dto.PageData;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageUtilTest {

    @Test
    public void testPageDataIsFilled() {
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getTotalPages()).thenReturn(5);
        Mockito.when(page.getNumber()).thenReturn(2);

        PageData pageData = PageUtil.getPageData(page);

        Assert.assertNull(pageData.getTotalPages());
        Assert.assertEquals(pageData.getPageNumbers(), IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList()));
    }

    @Test
    public void testPageDataIsLimitedToMaxOffsetOnRight() {
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getTotalPages()).thenReturn(500);
        Mockito.when(page.getNumber()).thenReturn(2);

        PageData pageData = PageUtil.getPageData(page);

        Assert.assertNotNull(pageData.getTotalPages());
        Assert.assertEquals(pageData.getPageNumbers(), IntStream.rangeClosed(1, 7).boxed().collect(Collectors.toList()));
    }

    @Test
    public void testPageDataIsLimitedToMaxOffsetOnBothSides() {
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getTotalPages()).thenReturn(800);
        Mockito.when(page.getNumber()).thenReturn(20);

        PageData pageData = PageUtil.getPageData(page);

        Assert.assertNotNull(pageData.getTotalPages());
        Assert.assertEquals(pageData.getCurrPage(), (Integer) 20);
        Assert.assertEquals(pageData.getPageNumbers(), IntStream.rangeClosed(15, 25).boxed().collect(Collectors.toList()));
    }

}