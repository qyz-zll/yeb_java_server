package com.zll.server.service;

import com.zll.server.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zll.server.pojo.Menu;
import com.zll.server.pojo.RespBean;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 张磊磊
 * @since 2022-11-25
 */
public interface IAdminService extends IService<Admin> {

    RespBean login(String username, String password, String code, HttpServletRequest request);

    Admin getAdminByUserName(String username);

    List<Menu> getMenusByAdminId();
}
