package com.zll.service.impl;

import com.kee.pojo.Admin;
import com.kee.mapper.AdminMapper;
import com.kee.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kee
 * @since 2021-03-10
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

}
