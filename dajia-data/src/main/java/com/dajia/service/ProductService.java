package com.dajia.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.dajia.domain.Price;
import com.dajia.domain.Product;
import com.dajia.domain.ProductImage;
import com.dajia.domain.ProductItem;
import com.dajia.domain.UserCart;
import com.dajia.domain.UserFavourite;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserOrderItem;
import com.dajia.repository.ProductItemRepo;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserCartRepo;
import com.dajia.repository.UserFavouriteRepo;
import com.dajia.repository.UserOrderItemRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.util.ApiKdtUtils;
import com.dajia.util.ApiWdUtils;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.ActiveStatus;
import com.dajia.util.CommonUtils.OrderStatus;
import com.dajia.util.CommonUtils.ProductStatus;
import com.dajia.vo.CartItemVO;
import com.dajia.vo.OrderVO;
import com.dajia.vo.ProductVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductService {
	Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ApiService apiService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private RewardService rewardService;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private UserFavouriteRepo favouriteRepo;

	@Autowired
	private UserCartRepo cartRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private UserOrderItemRepo orderItemRepo;

	@Autowired
	private UserRewardRepo rewardRepo;

	private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd_HH:mm:ss");

	public List<Product> loadProductsAllFromApiWd() {
		String token = "";
		try {
			token = apiService.loadApiWdToken();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.info("access token: " + token);
		String paramStr = ApiWdUtils.allProductsParamStr();
		String publicStr = ApiWdUtils.allProductsPublicStr(token);
		String allProductsUrl = ApiWdUtils.allProductsUrl();
		logger.info("allProductsUrl: " + allProductsUrl);
		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.getForObject(allProductsUrl, String.class, paramStr, publicStr);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		List<Product> productList = new ArrayList<Product>();
		try {
			productList = this.convertJson2Products(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return productList;
	}

	public List<Product> loadProductsAllFromApiKdt() {
		String retrunJsonStr = apiService.sendGet2Kdt(ApiKdtUtils.method_get_onsale_items, null);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		List<Product> productList = new ArrayList<Product>();
		try {
			productList = this.convertJson2Products(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return productList;
	}

	public Product loadProductFromApiWd(String refId) {
		String token = "";
		try {
			token = apiService.loadApiWdToken();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		String paramStr = ApiWdUtils.productParamStr(refId);
		String publicStr = ApiWdUtils.productPublicStr(token);
		String productUrl = ApiWdUtils.productUrl();
		logger.info("productUrl: " + productUrl);
		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.getForObject(productUrl, String.class, paramStr, publicStr);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		Product product = new Product();
		try {
			product = this.convertJson2Product(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return product;
	}

	public Product loadProductFromApiKdt(String refId) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(ApiKdtUtils.key_refid, refId);
		String retrunJsonStr = apiService.sendGet2Kdt(ApiKdtUtils.method_get_item, params);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		Product product = new Product();
		try {
			product = this.convertJson2Product(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return product;
	}

	public void syncProductsAll() {
		List<Product> products = this.loadProductsAllFromApiKdt();
		this.syncProducts(products);
	}

	public void syncProducts(List<Product> products) {
		for (Product product : products) {
			Product p = productRepo.findByRefId(product.refId);
			if (null != p) {
				if (p.isActive.equalsIgnoreCase(CommonUtils.ActiveStatus.NO.toString())) {
					continue; // skip inactive product
				}
				try {
					CommonUtils.copyProductProperties(product, p);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				this.initProductItems(p);
			} else {
				this.initProductItems(product);
			}
		}
	}

	public void initProductItems(Product product) {
		if (null == product.productItems || product.productItems.size() == 0) {
			ProductItem productItem = new ProductItem();
			productItem.isActive = CommonUtils.ActiveStatus.YES.toString();
			productItem.productStatus = CommonUtils.ProductStatus.INVALID.getKey();
			productItem.product = product;
			if (null == product.productItems) {
				product.productItems = new ArrayList<ProductItem>();
			}
			product.productItems.add(productItem);
		}
		productRepo.save(product);
	}

	public List<ProductVO> converProductVOListFromPI(List<ProductItem> productItemList) {
		List<ProductVO> productVOList = new ArrayList<ProductVO>();
		for (ProductItem pi : productItemList) {
			productVOList.add(loadProductDetail(pi.product.productId));
		}
		return productVOList;
	}

	public List<ProductVO> converProductVOListFromP(List<Product> productList) {
		List<ProductVO> productVOList = new ArrayList<ProductVO>();
		for (Product p : productList) {
			productVOList.add(loadProductDetail(p.productId));
		}
		return productVOList;
	}

	public ProductVO convertProductVO(Product product, ProductItem pi) {
		ProductVO productVO = new ProductVO();
		if (null == pi) {
			pi = loadProductItem(product);
		}
		if (null != pi) {
			calcPrice(pi);
			productVO.productItemId = pi.productItemId;
			productVO.sold = pi.sold;
			productVO.stock = pi.stock;
			productVO.buyQuota = pi.buyQuota;
			productVO.productStatus = pi.productStatus;
			productVO.fixTop = pi.fixTop;
			productVO.isPromoted = pi.isPromoted;
			productVO.originalPrice = pi.originalPrice;
			productVO.currentPrice = pi.currentPrice;
			productVO.postFee = pi.postFee;
			productVO.startDate = pi.startDate;
			productVO.expiredDate = pi.expiredDate;
			productVO.status4Show = getProductStatusStr(pi.productStatus);
			productVO.prices = pi.prices;
			productVO.targetPrice = pi.targetPrice;
			productVO.soldNeeded = pi.soldNeeded;
			productVO.priceOff = pi.priceOff;
			productVO.nextOff = pi.nextOff;
			productVO.progressValue = pi.progressValue;
		}
		productVO.productId = product.productId;
		productVO.refId = product.refId;
		productVO.shortName = product.shortName;
		productVO.name = product.name;
		productVO.brief = product.brief;
		productVO.description = product.description;
		productVO.spec = product.spec;
		productVO.totalSold = product.totalSold;
		productVO.imgUrl = product.imgUrl;
		productVO.imgUrl4List = product.imgUrl4List;
		productVO.productImages = product.productImages;
		productImageSort(productVO.productImages);
		return productVO;
	}

	@Transactional
	public ProductVO loadProductDetail(Long pid) {
		Product product = productRepo.findOne(pid);
		if (null == product) {
			return null;
		}
		product.productImages.size();
		return convertProductVO(product, null);
	}

	@Transactional
	public ProductVO loadProductDetailByItemId(Long itemId) {
		logger.info("productItemId: " + itemId);
		ProductItem pi = productItemRepo.findOne(itemId);
		if (null == pi) {
			return null;
		}
		return convertProductVO(pi.product, pi);
	}

	public ProductItem loadProductItem(Product product) {
		if (null == product) {
			return null;
		}
		product.productItems.size();
		if (null == product.productItems || product.productItems.size() == 0) {
			return null;
		}
		for (ProductItem pi : product.productItems) {
			if (pi.isActive.equalsIgnoreCase(CommonUtils.ActiveStatus.YES.toString())) {
				pi.prices.size();
				return pi;
			}
		}
		return null;
	}

	public List<ProductItem> loadAllValidProducts() {
		List<ProductItem> productItems = productItemRepo.findByProductStatusAndIsActiveOrderByExpiredDateAsc(
				ProductStatus.VALID.getKey(), ActiveStatus.YES.toString());
		return productItems;
	}

	public List<ProductItem> loadAllExpiredProducts() {
		List<ProductItem> productItems = productItemRepo.findByProductStatusAndIsActiveOrderByExpiredDateAsc(
				ProductStatus.EXPIRED.getKey(), ActiveStatus.YES.toString());
		return productItems;
	}

	public List<ProductVO> loadAllValidProductsWithPrices() {
		List<ProductItem> productItems = productItemRepo.findByProductStatusAndIsActiveOrderByExpiredDateAsc(
				ProductStatus.VALID.getKey(), ActiveStatus.YES.toString());
		for (ProductItem productItem : productItems) {
			calcPrice(productItem);
		}
		return converProductVOListFromPI(productItems);
	}

	public Page<ProductItem> loadAllValidProductsWithPricesByPage(Integer pageNum) {
		List<Integer> productStatusList = new ArrayList<Integer>();
		productStatusList.add(ProductStatus.VALID.getKey());
		productStatusList.add(ProductStatus.EXPIRED.getKey());
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage_5);
		Page<ProductItem> productItems = productItemRepo
				.findByProductStatusInAndStartDateBeforeAndIsActiveOrderByProductStatusAscFixTopDescExpiredDateAsc(
						productStatusList, new Date(), ActiveStatus.YES.toString(), pageable);
		for (ProductItem productItem : productItems) {
			calcPrice(productItem);
		}
		return productItems;
	}

	public Page<ProductItem> loadProductsByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<ProductItem> productItems = productItemRepo.findByIsActiveOrderByStartDateDesc(
				ActiveStatus.YES.toString(), pageable);
		for (ProductItem productItem : productItems) {
			productItem.status4Show = getProductStatusStr(productItem.productStatus);
		}
		return productItems;
	}

	public Page<ProductItem> loadProductsByKeywordByPage(String keyword, Integer pageNum) {
		List<Product> products = this.loadProductIdsByKeyword(keyword);
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<ProductItem> productItems = productItemRepo.findByProductInAndIsActiveOrderByStartDateDesc(products,
				ActiveStatus.YES.toString(), pageable);
		for (ProductItem productItem : productItems) {
			productItem.status4Show = getProductStatusStr(productItem.productStatus);
		}
		return productItems;
	}

	public List<Product> loadProductIdsByKeyword(String keyword) {
		List<Product> products = productRepo.findByNameContainingAndIsActiveOrderByCreatedDateDesc(keyword,
				ActiveStatus.YES.toString());
		return products;
	}

	public void getRealSold(Page<ProductItem> productItems) {
		for (ProductItem productItem : productItems) {
			Long realSold = 0L;
			List<Integer> orderStatusList = new ArrayList<Integer>();
			orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
			orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
			orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
			List<UserOrder> orderList = orderRepo.findByProductItemIdAndOrderStatusInAndIsActiveOrderByOrderDateDesc(
					productItem.productItemId, orderStatusList, CommonUtils.ActiveStatus.YES.toString());
			if (null != orderList) {
				for (UserOrder userOrder : orderList) {
					if (null != userOrder.paymentId) {
						realSold += userOrder.quantity;
					}
				}
			}
			List<UserOrderItem> orderItemList = orderItemRepo.findByProductItemIdAndIsActive(productItem.productItemId,
					CommonUtils.ActiveStatus.YES.toString());
			if (null != orderItemList) {
				for (UserOrderItem userOrderItem : orderItemList) {
					if (userOrderItem.userOrder.orderStatus == CommonUtils.OrderStatus.PAIED.getKey()
							|| userOrderItem.userOrder.orderStatus == CommonUtils.OrderStatus.DELEVERING.getKey()
							|| userOrderItem.userOrder.orderStatus == CommonUtils.OrderStatus.DELEVRIED.getKey()) {
						realSold += userOrderItem.quantity;
					}
				}
			}
			productItem.realSold = realSold;
		}
	}

	@Transactional
	public void productSold(UserOrder order) {
		// update order
		order.orderStatus = OrderStatus.PAIED.getKey();
		orderRepo.save(order);
		if (null != order.productItemId) {
			// update product price
			ProductItem productItem = productItemRepo.findOne(order.productItemId);
			updateProductPrice(productItem, order.quantity);
			if (null != order.refUserId) {
				// generate reward
				rewardService.createReward(order, null, productItem);
			}
		} else {
			for (UserOrderItem oi : order.orderItems) {
				// update product price
				ProductItem productItem = productItemRepo.findOne(oi.productItemId);
				updateProductPrice(productItem, oi.quantity);
				if (null != order.refUserId) {
					// generate reward
					rewardService.createReward(order, oi, productItem);
				}
			}
		}
	}

	private void updateProductPrice(ProductItem productItem, Integer quantity) {
		if (null != productItem) {
			if (null == productItem.sold) {
				productItem.sold = 0L;
			}
			productItem.sold += quantity;
			productItem.stock -= quantity;
			if (productItem.stock < 0L) {
				productItem.stock = 0L;
			}
			calcCurrentPrice(productItem, quantity);
		}
		productItemRepo.save(productItem);
	}

	public List<Product> loadFavProductsByUserId(Long userId) {
		List<UserFavourite> favourites = favouriteRepo.findByUserIdOrderByCreatedDateDesc(userId);
		List<Long> productIds = new ArrayList<Long>();
		for (UserFavourite favourite : favourites) {
			productIds.add(favourite.productId);
		}
		List<Product> products = productRepo.findByProductIdInAndIsActive(productIds, ActiveStatus.YES.toString());
		return products;
	}

	public List<Product> loadCartProductsByUserId(Long userId) {
		List<UserCart> cartItems = cartRepo.findByUserIdOrderByCreatedDateDesc(userId);
		List<Long> productIds = new ArrayList<Long>();
		for (UserCart cartItem : cartItems) {
			productIds.add(cartItem.productId);
		}
		List<Product> products = productRepo.findByProductIdInAndIsActive(productIds, ActiveStatus.YES.toString());
		return products;
	}

	/**
	 * 把某些productItem过期 这个方法独立出来以后可以给admin使用 比如对发生错误的 商品 可以手动走一遍到期流程
	 *
	 * @param productItems
	 * @param date
	 */
	@Transactional
	public void doExpireProductItems(List<ProductItem> productItems, Date date) {

		String jobToken = dateFormat.format(date);

		if (CollectionUtils.isEmpty(productItems)) {
			logger.warn("expire job {} finished, no product items", jobToken);
			return;
		}

		StringBuffer productItemsIds = new StringBuffer("product item [");
		for (ProductItem pi : productItems) {
			productItemsIds.append(pi.productItemId).append(",");
		}
		logger.info("expire job {}, {} to be deal with", jobToken, productItemsIds.append("]").toString());

		StringBuffer skippedProductItems = new StringBuffer("expire job {}, product items [");

		for (ProductItem productItem : productItems) {

			Long id = productItem.productItemId;

			try {
				if (null == productItem.expiredDate || productItem.expiredDate.before(date)) {
					productItem.productStatus = ProductStatus.EXPIRED.getKey();
					productItemRepo.save(productItem);
					orderService.orderRefund(productItem);
					logger.info("expire job {}, product item {} expired by time", jobToken, id);

				} else if (productItem.stock <= 0) {
					if (productItem.isPromoted.equalsIgnoreCase(CommonUtils.YesNoStatus.YES.toString())) {
						continue;
					}
					productItem.productStatus = ProductStatus.EXPIRED.getKey();
					productItemRepo.save(productItem);
					orderService.orderRefund(productItem);
					logger.info("expire job {}, product item {} expired by sold out", jobToken, id);

				} else {
					skippedProductItems.append(id).append(",");
				}

			} catch (Exception ex) {
				logger.error("expire job {}, product item {} failed", jobToken, id, ex);
			}
		}

		logger.info(skippedProductItems.append("] are skipped").toString(), jobToken);

		logger.info("expire job {} finished at {}", jobToken, System.currentTimeMillis());
	}

	/**
	 *
	 * @param date
	 */
	public void updateProductExpireStatus(Date date) {
		List<ProductItem> productItems = this.loadAllValidProducts();
		doExpireProductItems(productItems, date);
	}

	/**
	 * 计算本次价格
	 *
	 * 设本次销售了quantity件商品
	 * ========================================================
	 * ======================== 上次总销量 本次总销量 pdtItem.s-quantity pdtItem.s | | |
	 * |<------- new quantity ---->| | |<--- pdtItem.s - p3.s ---->|<--- p4.s -
	 * pdtItem.s -->|
	 * |-----|-------------|--------------------------------------
	 * ---------------| | | p3.s p4.s | p2.s p1.s
	 *
	 * ========================================================================
	 * =========
	 *
	 * @param productItem
	 * @param quantity
	 */
	private void calcCurrentPrice(ProductItem productItem, int quantity) {
		List<Price> prices = productItem.prices;
		for (Price price : prices) {
			// for edge logic
			if (price.sold < productItem.sold && price.sold > productItem.sold - quantity) {
				productItem.currentPrice = price.targetPrice;
				quantity = productItem.sold.intValue() - price.sold.intValue();
			}
			if (price.sold >= productItem.sold) {
				BigDecimal priceOff = productItem.currentPrice.add(price.targetPrice.negate()).divide(
						new BigDecimal(price.sold - productItem.sold + 1), 2, RoundingMode.HALF_UP);
				productItem.currentPrice = productItem.currentPrice.add(priceOff.multiply(new BigDecimal(quantity))
						.negate());
				break;
			}
		}
	}

	private BigDecimal calcNextOff(ProductItem productItem) {
		List<Price> prices = productItem.prices;
		long sold = productItem.sold == null ? 0L : productItem.sold + 1;
		for (Price price : prices) {
			if (price.sold >= sold) {
				return productItem.currentPrice.add(price.targetPrice.negate()).divide(
						new BigDecimal(price.sold - sold + 1), 2, RoundingMode.HALF_UP);
			}
		}
		return null;
	}

	private void calcPrice(ProductItem productItem) {
		BigDecimal targetPrice = productItem.originalPrice;
		if (null != targetPrice) {
			long soldNeeded = 0L;
			for (Price price : productItem.prices) {
				if (price.targetPrice.compareTo(targetPrice) < 0) {
					targetPrice = price.targetPrice;
					soldNeeded = price.sold;
				}
			}
			if (null != productItem.currentPrice) {
				productItem.targetPrice = targetPrice;
				productItem.soldNeeded = soldNeeded - CommonUtils.getLongValue(productItem.sold);
				productItem.priceOff = productItem.originalPrice.add(productItem.currentPrice.negate());
				productItem.nextOff = calcNextOff(productItem);
				productItem.progressValue = calcProgress(productItem);
			}
		}
	}

	private long calcProgress(ProductItem productItem) {
		BigDecimal totalOff = productItem.originalPrice.add(productItem.targetPrice.negate());
		if (totalOff.longValue() == 0) {
			return 100L;
		}
		Double progress = productItem.priceOff.divide(totalOff, 2, RoundingMode.HALF_UP).doubleValue() * 100;
		return progress.longValue();
	}

	private List<Product> convertJson2Products(String jsonStr) throws JsonParseException, JsonMappingException,
			IOException {
		List<Product> productList = new ArrayList<Product>();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map = mapper.readValue(jsonStr, HashMap.class);
		List<Map> itemList = (List) ((Map) map.get("response")).get("items");
		for (Map itemMap : itemList) {
			productList.add(ApiKdtUtils.productMapper(itemMap));
		}
		return productList;
	}

	private Product convertJson2Product(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map = mapper.readValue(jsonStr, HashMap.class);
		Map itemMap = (Map) map.get("response");
		return ApiKdtUtils.productMapper(itemMap);
	}

	private String getProductStatusStr(Integer key) {
		if (null == key) {
			return null;
		}
		String returnStr = null;
		if (key.equals(ProductStatus.INVALID.getKey())) {
			returnStr = ProductStatus.INVALID.getValue();
		} else if (key.equals(ProductStatus.VALID.getKey())) {
			returnStr = ProductStatus.VALID.getValue();
		} else if (key.equals(ProductStatus.EXPIRED.getKey())) {
			returnStr = ProductStatus.EXPIRED.getValue();
		}
		return returnStr;
	}

	public void removeProduct(Long productId) {
		Product product = productRepo.findOne(productId);
		if (null != product) {
			product.isActive = CommonUtils.ActiveStatus.NO.toString();
			if (null != product.productItems && product.productItems.size() > 0) {
				for (ProductItem productItem : product.productItems) {
					if (productItem.isActive.equalsIgnoreCase(CommonUtils.ActiveStatus.YES.toString())) {
						productItem.isActive = CommonUtils.ActiveStatus.NO.toString();
					}
				}
			}
			productRepo.save(product);
		}
	}

	public List<ProductVO> loadProducts4Order(List<UserOrderItem> orderItemList) {
		List<ProductVO> productVOList = new ArrayList<ProductVO>();
		for (UserOrderItem oi : orderItemList) {
			ProductVO productVO = this.loadProductDetailByItemId(oi.productItemId);
			productVOList.add(productVO);
		}
		return productVOList;
	}

	public List<ProductItem> loadProductItemsByTrackingId(String trackingId) {
		List<ProductItem> productItems = new ArrayList<ProductItem>();
		List<UserOrderItem> orderItems = orderItemRepo.findByTrackingIdAndIsActive(trackingId,
				CommonUtils.ActiveStatus.YES.toString());
		for (UserOrderItem orderItem : orderItems) {
			ProductItem pi = productItemRepo.findOne(orderItem.productItemId);
			if (null != pi) {
				productItems.add(pi);
			}
		}
		return productItems;
	}

	public boolean validateStock(OrderVO orderVO) {
		if (null != orderVO.productId) {
			if (this.loadProductDetail(orderVO.productId).stock <= 0) {
				return false;
			}
		} else {
			if (null != orderVO.cartItems) {
				for (CartItemVO cartItem : orderVO.cartItems) {
					if (this.loadProductDetail(cartItem.productId).stock <= 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void productImageSort(List<ProductImage> imgList) {
		if (null != imgList) {
			for (int i = 0; i < imgList.size() - 1; i++) {
				for (int j = 0; j < imgList.size() - 1 - i; j++) {
					if (imgList.get(j).sort > imgList.get(j + 1).sort) {
						ProductImage temp = imgList.get(j);
						imgList.set(j, imgList.get(j + 1));
						imgList.set(j + 1, temp);
					}
				}
			}
		}
	}
}
