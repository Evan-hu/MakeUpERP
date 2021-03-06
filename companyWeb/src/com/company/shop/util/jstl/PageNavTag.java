/**
 * Copyright (c) 2010 Noah
 * All right reserved.
 */
package com.company.shop.util.jstl;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.company.pager.Page;

/**
 * 分页导航.
 */
public class PageNavTag extends SimpleTagSupport {

	private Page<?> page;
	private String	url;
	
	private final static int MAX_SHOW_PAGE_COUNT = 10;

	@Override
	public void doTag() throws JspException, IOException {
		if (page != null) {
			page.setUrl(url);
			String url = page.getAllUrl();
			if (url.indexOf("?") > -1) {
				url += "&";
			} else {
				url += "?";
			}
			int nowPage = page.getPageNo();
			int last = page.getTotalPage();
			int pre = nowPage <= 1 ? 1 : (nowPage - 1);
			int follow = nowPage >= last ? nowPage : nowPage + 1;
			int segment = (nowPage - 1) / MAX_SHOW_PAGE_COUNT + 1;
			int startPageNumber = (segment - 1) * MAX_SHOW_PAGE_COUNT + 1;
			int endPageNumber = segment * MAX_SHOW_PAGE_COUNT;
			startPageNumber = startPageNumber < 1 ? 1 : startPageNumber;
			endPageNumber = endPageNumber > last ? last : endPageNumber;

			StringBuilder html = new StringBuilder();
			if (page.getTotalPage() > 1) {

				html.append("<div class='page'>");
				html.append("<a href='").append(url).append("pageNo=1")
						.append("'>首页&nbsp;</a>");

				if (!page.isFirst()) {
					html.append("<a href='").append(url).append("pageNo=")
							.append(pre).append("'>&nbsp;上一页&nbsp;</a>");
				}

				if (startPageNumber > 1) {
					html.append("<a href='").append(url).append("pageNo=")
							.append(nowPage - 2).append("'>...</a>");
				}

				for (int i = startPageNumber; i <= endPageNumber; i++) {
					if (i == nowPage) {
						html.append("<a href='#' class='current'>&nbsp;").append(i)
								.append("&nbsp;</a>");
					} else {
						html.append("<a href='").append(url).append("pageNo=")
								.append(i).append("'>&nbsp;").append(i)
								.append("&nbsp;</a>");
					}
				}

				if (endPageNumber < last) {
					html.append("<a href='").append(url).append("pageNo=")
							.append(nowPage + 2).append("'>...</a>");
				}

				if (!page.isLast()) {
					html.append("<a href='").append(url).append("pageNo=")
							.append(follow).append("'>&nbsp;下一页&nbsp;</a>");
				}

				html.append("<a href='").append(url).append("pageNo=")
						.append(last).append("'>&nbsp;尾页</a>");
				html.append("</div>");
			}

			Writer out = getJspContext().getOut();
			out.write(html.toString());
		}
	}
	
	public Page<?> getPage() {
		return page;
	}

	public void setPage(Page<?> page) {
		this.page = page;
	}
	
  public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
