package com.dajia.util;

import com.dajia.domain.Product;
import com.dajia.domain.ProductImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiKdtUtils {
	public static final String appkey = "appkey_kdt";
	public static final String secret = "secret_kdt";

	public static final String key_refid = "num_iid";

	public static final String method_get_item = "kdt.item.get";
	public static final String method_get_onsale_items = "kdt.items.onsale.get";
	public static final String method_update_item = "kdt.item.update";

	public static Product productMapper(Map itemMap) {
		Product product = new Product();
		product.refId = (String) itemMap.get(key_refid);
		product.name = (String) itemMap.get("title");
		product.shortName = (String) itemMap.get("title");
		product.description = (String) itemMap.get("desc");
		// product.stock = Long.valueOf((String) itemMap.get("num"));
		// product.sold = ((Integer) itemMap.get("sold_num")).longValue();
		// product.buyQuota = Integer.valueOf((String)
		// itemMap.get("buy_quota"));
		// product.currentPrice = new BigDecimal((String) itemMap.get("price"));
		// product.postFee = new BigDecimal((String) itemMap.get("post_fee"));
		product.imgUrl = (String) itemMap.get("pic_url");
		// product.imgUrl4List = (String) itemMap.get("pic_thumb_url");

		List<Map<String, String>> imgMaps = (List<Map<String, String>>) itemMap.get("item_imgs");
		List<ProductImage> productImgs = new ArrayList<ProductImage>();
		if (null != imgMaps && imgMaps.size() > 0) {
			for (int i = 0; i < imgMaps.size(); i++) {
				Map<String, String> imgMap = imgMaps.get(i);
				if (i == 0) {
					continue;
				} else {
					if (null == product.imgUrl4List || product.imgUrl4List.length() == 0) {
						product.imgUrl4List = imgMap.get("thumbnail");
						if (null != product.imgUrl4List) {
							product.imgUrl4List = product.imgUrl4List.replaceAll("https://", "http://");
						}
					}
					ProductImage pi = new ProductImage();
					pi.url = imgMap.get("url");
					pi.thumbUrl = imgMap.get("thumbnail");
					pi.medUrl = imgMap.get("medium");
					pi.product = product;
					productImgs.add(pi);
				}
			}
		}
		product.productImages = productImgs;
		return product;
	}
}