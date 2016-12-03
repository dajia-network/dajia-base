package com.dajia.util;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dajia.domain.Price;
import com.dajia.domain.Product;
import com.dajia.domain.ProductImage;
import com.dajia.domain.ProductItem;
import com.dajia.vo.PaginationVO;
import com.dajia.vo.ProductVO;

public class CommonUtils {

	public static final String return_val_success = "success";
	public static final String return_val_failed = "failed";

	public static final Long beijing_city_key = 110100L;
	public static final Long shanghai_city_key = 310100L;

	public static final Integer page_item_perpage = 20;
	public static final Integer page_item_perpage_5 = 5;
	public static final Integer page_range_limit = 10;

	public static final String cache_name_signup_code = "sms_signup_code";
	public static final String cache_name_signin_code = "sms_signin_code";
	public static final String cache_name_binding_code = "sms_binding_code";

	public static final String global_cache_key = "global_cache";

	public static final Integer reward_delay_days = 8;

	public static final String refund_type_refund = "Refund";
	public static final String refund_type_reward = "Reward";

	public static final String ref_user_id = "refUserId";
	public static final String product_id = "productId";
	public static final String ref_order_id = "refOrderId";
	public static final String null_string = "null";
	public static final String state_string = "STATE";

	public static String stringCharsetConvert(String str, String charset) {
		ByteBuffer bf = Charset.forName(charset).encode(str);
		return new String(bf.array());
	}

	public static PaginationVO generatePaginationVO(Page page, Integer currentPageIdx) {
		PaginationVO pv = new PaginationVO();
		pv.results = page.getContent();
		pv.totalPages = page.getTotalPages();
		pv.totalCount = page.getNumberOfElements();
		pv.currentPage = currentPageIdx;
		pv.hasPrev = page.hasPrevious();
		pv.hasNext = page.hasNext();
		if (page.getTotalPages() <= page_range_limit) {
			pv.startPage = 1;
			pv.endPage = page.getTotalPages();
		} else if (currentPageIdx <= page_range_limit) {
			pv.startPage = 1;
			pv.endPage = page_range_limit;
		} else {
			pv.startPage = currentPageIdx - page_range_limit + 1;
			pv.endPage = currentPageIdx;
		}
		pv.pageRange = new ArrayList<Integer>();
		for (int i = pv.startPage; i <= pv.endPage; i++) {
			pv.pageRange.add(i);
		}
		return pv;
	}

	public static void copyProperties(Object src, Object target) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = src.getClass().getFields();
		for (Field field : fields) {
			if (null != field.get(src)) {
				field.set(target, field.get(src));
			}
		}
	}

	public static void copyProductProperties(Product src, Product target) {
		// target.name = src.name;
		target.description = src.description;
		// target.postFee = src.postFee;
		target.imgUrl = src.imgUrl;
		target.imgUrl4List = src.imgUrl4List;
		if (null != src.productImages && src.productImages.size() > 0) {
			for (ProductImage pi : src.productImages) {
				pi.product = target;
			}
			target.productImages.clear();
			target.productImages.addAll(src.productImages);
		}
	}

	public static void updateProductItemWithReq(ProductItem persist, ProductVO req) {
		if (null != req.stock) {
			persist.stock = req.stock;
		}
		if (null != req.buyQuota) {
			persist.buyQuota = req.buyQuota;
		}
		if (null != req.startDate) {
			persist.startDate = req.startDate;
		}
		if (null != req.expiredDate) {
			persist.expiredDate = req.expiredDate;
		}
		if (null != req.fixTop) {
			persist.fixTop = req.fixTop;
		}
		if (null != req.isPromoted) {
			persist.isPromoted = req.isPromoted;
		}
		if (null != req.productStatus) {
			persist.productStatus = req.productStatus;
		} else {
			persist.productStatus = ProductStatus.INVALID.getKey();
		}
		if (null != req.originalPrice) {
			if (persist.originalPrice == null || req.originalPrice.compareTo(persist.originalPrice) != 0) {
				persist.currentPrice = req.originalPrice;
			}
			persist.originalPrice = req.originalPrice;
		}
		if (null != req.postFee) {
			persist.postFee = req.postFee;
		}
		if (null != req.prices && req.prices.size() > 0) {
			for (Price price : req.prices) {
				price.productItem = persist;
			}
			if (null != persist.prices) {
				persist.prices.clear();
				persist.prices.addAll(req.prices);
			} else {
				persist.prices = req.prices;
			}
		} else if (null != persist.prices) {
			persist.prices.clear();
		}
	}

	public static void updateProductWithReq(Product persist, ProductVO req) {
		if (null != req.name) {
			persist.name = req.name;
		}
		if (null != req.shortName) {
			persist.shortName = req.shortName;
		}
		if (null != req.brief) {
			persist.brief = req.brief;
		}
		if (null != req.description) {
			persist.description = req.description;
		}
		persist.imgUrl = req.imgUrl;
		if (null != req.productImages) {
			persist.imgUrl4List = null;
			for (ProductImage productImage : req.productImages) {
				productImage.product = persist;
				if (null == persist.imgUrl4List) {
					persist.imgUrl4List = productImage.url;
					if (null != persist.imgUrl4List) {
						persist.imgUrl4List = persist.imgUrl4List.replaceAll("https://", "http://");
					}
				}
			}
			if (null != persist.productImages) {
				persist.productImages.clear();
				persist.productImages.addAll(req.productImages);
			} else {
				persist.productImages = req.productImages;
			}
		}
	}

	public static long getLongValue(Long input) {
		return null != input ? input.longValue() : 0L;
	}

	public static String genRandomNum(int pwd_len) {
		return RandomStringUtils.randomNumeric(pwd_len);
	}

	public static String genTrackingId(Long userId) {
		StringBuilder trackingId = new StringBuilder();
		String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String ranNum = String.valueOf(Math.round(Math.random() * 1000));
		trackingId.append(dateStr).append(userId).append(ranNum);
		return trackingId.toString();
	}

	public static boolean checkParameterIsNull(String param) {
		return null == param || param.isEmpty() || param.equalsIgnoreCase(null_string);
	}

	public static String subString(String str, int length) {
		if (null == str) {
			return null;
		}
		if (str.length() > length) {
			return str.substring(0, length) + "...";
		} else {
			return str;
		}
	}

	public static String getRequestIP(HttpServletRequest request) {
		if (null == request) {
			return "";
		}
		String ipAddr;
		ipAddr = request.getHeader("X-Real-IP");
		if (null == ipAddr || ipAddr.length() == 0) {
			ipAddr = request.getHeader("x-forwarded-for");
		}
		if (null == ipAddr || ipAddr.length() == 0) {
			ipAddr = "127.0.0.1";
		}
		return ipAddr;
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	public enum ActiveStatus {
		YES("Y"), NO("N");
		private String key;

		private ActiveStatus(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return String.valueOf(this.key);
		}
	}

	public enum YesNoStatus {
		YES("Y"), NO("N");
		private String key;

		private YesNoStatus(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return String.valueOf(this.key);
		}
	}

	public enum ProductShared {
		YES("Y"), NO("N");
		private String key;

		private ProductShared(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return String.valueOf(this.key);
		}
	}

	public enum LocationType {
		PROVINCE("province"), CITY("city"), AREA("area");
		private String key;

		private LocationType(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return String.valueOf(this.key);
		}
	}

	public enum OrderStatus {
		PENDING_PAY(1, "待付款"), PAIED(2, "待发货"), DELEVERING(3, "已发货"), DELEVRIED(4, "已签收"), CLOSED(5, "已关闭"), CANCELLED(
				6, "已取消");
		private Integer key;
		private String value;

		private OrderStatus(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public static String getOrderStatusStr(Integer key) {
		String returnStr = null;
		if (key.equals(OrderStatus.PENDING_PAY.getKey())) {
			returnStr = OrderStatus.PENDING_PAY.getValue();
		} else if (key.equals(OrderStatus.PAIED.getKey())) {
			returnStr = OrderStatus.PAIED.getValue();
		} else if (key.equals(OrderStatus.DELEVERING.getKey())) {
			returnStr = OrderStatus.DELEVERING.getValue();
		} else if (key.equals(OrderStatus.DELEVRIED.getKey())) {
			returnStr = OrderStatus.DELEVRIED.getValue();
		} else if (key.equals(OrderStatus.CLOSED.getKey())) {
			returnStr = OrderStatus.CLOSED.getValue();
		} else if (key.equals(OrderStatus.CANCELLED.getKey())) {
			returnStr = OrderStatus.CANCELLED.getValue();
		}
		return returnStr;
	}

	public enum ProductStatus {
		INVALID(1, "下架"), VALID(2, "上架"), EXPIRED(3, "打价已结束");
		private Integer key;
		private String value;

		private ProductStatus(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public enum PayType {
		WECHAT(1, "wx_pub"), ALIPAY(2, "alipay_wap");
		private Integer key;
		private String value;

		private PayType(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public static String getPayTypeStr(Integer key) {
		String returnStr = null;
		if (key.equals(PayType.WECHAT.getKey())) {
			returnStr = PayType.WECHAT.getValue();
		} else if (key.equals(PayType.ALIPAY.getKey())) {
			returnStr = PayType.ALIPAY.getValue();
		}
		return returnStr;
	}

	public enum RewardStatus {
		INVALID(0, "尚未生效"), PENDING(1, "待退款"), COMPLETED(2, "已退款"), CANCELLED(5, "已取消"), ERROR(3, "出错"), SALES(4, "推广");
		private Integer key;
		private String value;

		private RewardStatus(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public enum RefundType {
		REFUND(0, "差价退款"), REWARD(1, "推荐奖励"), MANNUAL(2, "人工退款");
		private Integer key;
		private String value;

		private RefundType(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public static String getRefundTypeStr(Integer key) {
		String returnStr = null;
		if (key.equals(RefundType.REFUND.getKey())) {
			returnStr = RefundType.REFUND.getValue();
		} else if (key.equals(RefundType.REWARD.getKey())) {
			returnStr = RefundType.REWARD.getValue();
		} else if (key.equals(RefundType.MANNUAL.getKey())) {
			returnStr = RefundType.MANNUAL.getValue();
		}
		return returnStr;
	}

	public enum RefundStatus {
		PENDING(0, "退款中"), COMPLETE(1, "退款完成"), FAILED(2, "退款失败"), RETRYING(3, "重试退款中");
		private Integer key;
		private String value;

		private RefundStatus(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public static String getRefundStatusStr(Integer key) {
		String returnStr = null;
		if (key.equals(RefundStatus.PENDING.getKey())) {
			returnStr = RefundStatus.PENDING.getValue();
		} else if (key.equals(RefundStatus.COMPLETE.getKey())) {
			returnStr = RefundStatus.COMPLETE.getValue();
		} else if (key.equals(RefundStatus.FAILED.getKey())) {
			returnStr = RefundStatus.FAILED.getValue();
		} else if (key.equals(RefundStatus.RETRYING.getKey())) {
			returnStr = RefundStatus.RETRYING.getValue();
		}
		return returnStr;
	}

	public enum LogisticAgent {
		SHUNFENG("shunfeng", "顺丰快递"), TIANTIAN("tiantian", "天天快递"), ZHONGTONG("zhongtong", "中通快递"), SHENTONG(
				"shentong", "申通快递"), YUNDA("yunda", "韵达快递"), HUITONG("huitongkuaidi", "百世汇通"), EMS("ems", "邮政EMS");
		private String key;
		private String value;

		private LogisticAgent(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public static String getLogisticAgentStr(String key) {
		String returnStr = null;
		if (null != key) {
			if (key.equals(LogisticAgent.TIANTIAN.getKey())) {
				returnStr = LogisticAgent.TIANTIAN.getValue();
			} else if (key.equals(LogisticAgent.SHUNFENG.getKey())) {
				returnStr = LogisticAgent.SHUNFENG.getValue();
			} else if (key.equals(LogisticAgent.ZHONGTONG.getKey())) {
				returnStr = LogisticAgent.ZHONGTONG.getValue();
			} else if (key.equals(LogisticAgent.SHENTONG.getKey())) {
				returnStr = LogisticAgent.SHENTONG.getValue();
			} else if (key.equals(LogisticAgent.YUNDA.getKey())) {
				returnStr = LogisticAgent.YUNDA.getValue();
			} else if (key.equals(LogisticAgent.HUITONG.getKey())) {
				returnStr = LogisticAgent.HUITONG.getValue();
			}
		}
		return returnStr;
	}

	public enum ProductImageType {
		HOME(0, "首页图"), LIST(1, "列表图"), NORMAL(1, "常规图");
		private Integer key;
		private String value;

		private ProductImageType(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public enum LogType {
		SIMPLE_SHARE(1, "直接分享"), REWARD_SHARE(2, "奖励分享"), PRODUCT_VISIT(3, "产品页面访问");
		private Integer key;
		private String value;

		private LogType(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public enum ShareType {
		BUY_SHARE(1, "购买分享");
		private Integer key;
		private String value;

		private ShareType(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	public static String Y = YesNoStatus.YES.toString();
	public static String N = YesNoStatus.NO.toString();
}