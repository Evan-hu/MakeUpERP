package com.company.shop.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.company.shop.exception.BizException;

/**
 * 获取站点配置信息
 * 
 * @author Administrator
 * 
 */
public class SiteConfig {
	private static SiteConfig siteConfig = new SiteConfig();

	private String siteRoot;
	//图片路径
	private String fileRoot;
	//文档路径
	private String docRoot;
	private String lunceneIndexRoot;

	public static SiteConfig getInstance(){
		return siteConfig;
	}
	
	private SiteConfig() {
		InputStream fis = null;
		try {
			Properties properties = new Properties();
			fis = SiteConfig.class.getResourceAsStream("/config.properties");
			properties.load(fis);
			siteRoot = properties.getProperty("SITE_ROOT");
			fileRoot = properties.getProperty("FILE_ROOT");
			this.docRoot = properties.getProperty("DOC_ROOT");
			lunceneIndexRoot = properties.getProperty("LUCENEINDEX_ROOT");
		} catch (IOException e) {
			throw new BizException("加载模板配置异常");
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception ex) {
					new BizException("加载模板配置异常");
				}
			}
		}			
	}

	public String getSiteRoot() {
		return siteRoot;
	}

	public String getFileRoot() {
		return fileRoot;
	}

	public String getLunceneIndexRoot() {
		return lunceneIndexRoot;
	}

  public String getDocRoot() {
    return docRoot;
  }	
}
