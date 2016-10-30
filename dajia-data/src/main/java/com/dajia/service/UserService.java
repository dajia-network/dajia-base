package com.dajia.service;

import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.repository.UserRepo;
import com.dajia.util.*;
import com.dajia.util.CommonUtils.ActiveStatus;
import com.dajia.util.CommonUtils.YesNoStatus;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.SalesIndicatorVO;
import com.dajia.vo.SalesVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {
	Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private OrderService orderService;

	public String checkMobile(String mobile) {
		String returnVal = CommonUtils.return_val_failed;
		if (null == userRepo.findByMobile(mobile)) {
			returnVal = CommonUtils.return_val_success;
		}
		return returnVal;
	}

	public User userSignup(User user, HttpServletRequest request) {
		user.password = EncodingUtil.encode("SHA1", user.password);
		user.userName = UserUtils.generateUserName(user.mobile);
		user.isAdmin = "N";
		user.isSales = "N";
		user.lastVisitIP = CommonUtils.getRequestIP(request);
		user.lastVisitDate = new Date();
		userRepo.save(user);
		return user;
	}

	public User oauthLogin(String oauthType, String oauthUserId, Map<String, String> userInfoMap, String state,
                           HttpServletRequest request) {
		User user = userRepo.findByOauthUserIdAndOauthType(oauthUserId, oauthType);
		if (null == user) {
			user = new User();
			user.oauthType = ApiWechatUtils.wechat_oauth_type;
			user.oauthUserId = oauthUserId;
			user.isAdmin = "N";
			user.isSales = "N";
			if (null != state && !state.equalsIgnoreCase(CommonUtils.state_string)) {
				String[] stateArray = state.split("_");
				if (stateArray.length > 1) {
					String refUserId = stateArray[0];
					user.refUserId = Long.valueOf(refUserId);
				}
			}
		}
		user.lastVisitIP = CommonUtils.getRequestIP(request);
		user.lastVisitDate = new Date();
		ApiWechatUtils.updateWechatUserInfo(user, userInfoMap);
		userRepo.save(user);
		return user;
	}

	public User oauthLogin(String oauthType, String oauthUserId, HttpServletRequest request) {
		User user = userRepo.findByOauthUserIdAndOauthType(oauthUserId, oauthType);
		if (null != user) {
			user.lastVisitIP = CommonUtils.getRequestIP(request);
			user.lastVisitDate = new Date();
			userRepo.save(user);
		}
		return user;
	}

	public User userLogin(String mobile, String password, HttpServletRequest request, boolean authIgnore) {
		User user = userRepo.findByMobile(mobile);
		password = EncodingUtil.encode("SHA1", password);
		if ((null == user || null == user.password || !user.password.equals(password)) && !authIgnore) {
			return null;
		} else {
			user.lastVisitIP = CommonUtils.getRequestIP(request);
			user.lastVisitDate = new Date();
			userRepo.save(user);
		}

		return user;
	}

	/**
	 * 用户名密码登录
	 *
	 * @param loginUserVO
	 * @return
	 */
	public User userPassLogin(LoginUserVO loginUserVO) {

		if (null == loginUserVO) {
			logger.error("login failed, type={}, error={}", "userPass", "input loginUserVo is null");
			return null;
		}

		if (StringUtils.isEmpty(loginUserVO.userName)) {
			logger.error("login failed, type={}, user={}, error={}", "userPass", loginUserVO, "username is empty");
			return null;
		}

		if (StringUtils.isEmpty(loginUserVO.password)) {
			logger.error("login failed, type={}, user={}, error={}", "userPass", loginUserVO, "password is empty");
			return null;
		}

		loginUserVO.password = EncodingUtil.encode("SHA1", loginUserVO.password);

		try {
			User user = userRepo.findByUserNameAndPasswordAndIsActiveAndIsAdmin(loginUserVO.userName, loginUserVO.password, CommonUtils.Y, CommonUtils.Y);

			if(null == user) {
				logger.error("login failed, type={}, user={}, error={}", "userPass", loginUserVO, "no such user, check username, password, active, isAdmin");
				return null;
			}

			user.lastVisitDate = loginUserVO.loginDate;
			user.lastVisitIP = loginUserVO.loginIP;

			logger.info("login succeed, type={}, user={}", "userPass", loginUserVO);
			return user;

		} catch (Exception ex) {
			logger.error("login failed, type={}, user={}, error={}", "userPass", loginUserVO, "db error: " + ex.getMessage());
			return null;
		}
	}



	public String userLogout(Long userId, HttpServletRequest request) {
		String returnVal = CommonUtils.return_val_failed;
		if (null != userRepo.findByUserId(userId)) {
			request.getSession().setAttribute(UserUtils.session_user, null);
			returnVal = CommonUtils.return_val_success;
		}
		return returnVal;
	}

	public Page<User> loadUsersByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<User> users = userRepo.findByIsActiveOrderByCreatedDateDesc(ActiveStatus.YES.toString(), pageable);
		return users;
	}

	public Page<User> loadSalesUsersByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<User> users = userRepo.findByIsSalesAndIsActiveOrderByCreatedDateDesc(YesNoStatus.YES.toString(),
				ActiveStatus.YES.toString(), pageable);
		return users;
	}

	public Page<User> loadUsersByKeywordByPage(String keyword, Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<User> users = userRepo.findByUserNameContainingAndIsActiveOrderByCreatedDateDesc(keyword,
				ActiveStatus.YES.toString(), pageable);
		return users;
	}

	public String bindMobile(Long userId, String mobile) {
		String returnVal = CommonUtils.return_val_failed;
		User user = userRepo.findByUserId(userId);
		if (null != user) {
			user.mobile = mobile;
			userRepo.save(user);
			returnVal = CommonUtils.return_val_success;
		}
		return returnVal;
	}

	public LoginUserVO getUserVO(Long userId) {
		User user = userRepo.findByUserId(userId);
		return UserUtils.getUserVO(user);
	}

	public void modifyUser(Long userId, LoginUserVO userVO) {
		User user = userRepo.findByUserId(userId);
		user.isAdmin = userVO.isAdmin;
		user.isSales = userVO.isSales;
		userRepo.save(user);
	}

	public SalesVO generateSalesVO(User user) {
		SalesVO sales = UserUtils.getSalesVO(user);
		sales.refAmountWTD = new BigDecimal(0);
		sales.bonusAmountWTD = new BigDecimal(0);

		Calendar weekStart = Calendar.getInstance();
		weekStart.set(Calendar.DAY_OF_WEEK, 1);
		weekStart.set(Calendar.HOUR_OF_DAY, 0);
		weekStart.set(Calendar.MINUTE, 0);
		weekStart.set(Calendar.SECOND, 0);
		Date startDate = weekStart.getTime();
		List<UserOrder> orderList = orderService.getOrderListBySales(user.userId, startDate, new Date());
		for (UserOrder order : orderList) {
			sales.refAmountWTD = sales.refAmountWTD.add(order.totalPrice);
			BigDecimal coef = null;
			if (order.totalPrice.compareTo(new BigDecimal(100)) >= 0) {
				coef = new BigDecimal(0.15);
			} else {
				coef = new BigDecimal(0.2);
			}
			sales.bonusAmountWTD = sales.bonusAmountWTD.add(order.totalPrice.multiply(coef));
		}
		sales.refOrderNumWTD = orderList.size();
		List<User> users = userRepo.findByRefUserIdAndCreatedDateBetweenAndIsActive(user.userId, startDate, new Date(),
				CommonUtils.ActiveStatus.YES.toString());
		sales.refUserNumWTD = users.size();

		return sales;
	}

	public SalesVO getSalesDetail(SalesVO salesVO) {
		salesVO.salesIndicators = new ArrayList<SalesIndicatorVO>();
		Date startDate = new Date();
		Date endDate = new Date();
		for (int i = 0; i < 4; i++) {
			Calendar weekStart = Calendar.getInstance();
			weekStart.setTime(startDate);
			if (i > 0) {
				weekStart.add(Calendar.DATE, -1);
			}
			weekStart.set(Calendar.DAY_OF_WEEK, 1);
			weekStart.set(Calendar.HOUR_OF_DAY, 0);
			weekStart.set(Calendar.MINUTE, 0);
			weekStart.set(Calendar.SECOND, 0);
			startDate = weekStart.getTime();

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			String startDateStr = dateFormat.format(startDate);
			String endDateStr = dateFormat.format(endDate);
			String periodStr = startDateStr + " - " + endDateStr;

			SalesIndicatorVO indicator = new SalesIndicatorVO();
			indicator.period = periodStr;

			indicator.refAmount = new BigDecimal(0);
			indicator.bonusAmount = new BigDecimal(0);
			List<UserOrder> orderList = orderService.getOrderListBySales(salesVO.userId, startDate, endDate);
			for (UserOrder order : orderList) {
				indicator.refAmount = indicator.refAmount.add(order.totalPrice);
				BigDecimal coef = null;
				if (order.totalPrice.compareTo(new BigDecimal(100)) >= 0) {
					coef = new BigDecimal(0.15);
				} else {
					coef = new BigDecimal(0.2);
				}
				indicator.bonusAmount = indicator.bonusAmount.add(order.totalPrice.multiply(coef));
			}
			indicator.refOrderNum = orderList.size();
			List<User> users = userRepo.findByRefUserIdAndCreatedDateBetweenAndIsActive(salesVO.userId, startDate,
					endDate, CommonUtils.ActiveStatus.YES.toString());
			indicator.refUserNum = users.size();
			salesVO.salesIndicators.add(indicator);

			endDate = startDate;
		}

		return salesVO;
	}

	public SalesVO getSalesVO(Long userId) {
		User user = userRepo.findByUserId(userId);
		if (null != user) {
			SalesVO salesVO = generateSalesVO(user);
			return getSalesDetail(salesVO);
		}
		return null;
	}

	/**
	 * 修改用户密码
	 *
	 * @param userId
	 * @param oldPass
	 * @param newPass
	 * @return
	 */
	public boolean updateUserPassword(Long userId, String oldPass, String newPass) {
		String encodedOldPass = EncodingUtil.encode("SHA1", oldPass);
		String encodedNewPass = EncodingUtil.encode("SHA1", newPass);
		logger.info("update user password, userId={}, oldPass={}, newPass={}", userId, encodedOldPass, encodedNewPass);

		int updateCount = 0;
		try {
			updateCount = userRepo.updateUserPass(userId, encodedOldPass, encodedNewPass);
			logger.info("update user password result, userId={}, result={}", userId, updateCount);
		} catch (Exception ex) {
			logger.error("update user password failed, userId={}, reason={}", userId, ex.getMessage());
		}
		return updateCount > 0;
	}

	/**
	 * 用户是否存在
	 *
	 * @param userId
	 * @return
	 */
	public DajiaResult canFoundUser(Long userId) {
		if (null == userId || userId <= 0) {
			return DajiaResult.inputError("用户ID为空", null);
		}

		try {
			if (!userRepo.exists(userId)) {
				return DajiaResult.notFound("用户不存在", null);
			}
			return DajiaResult.success();

		} catch (Exception ex) {
			return DajiaResult.systemError("查找用户失败,系统异常", null, ex);
		}
	}
}
