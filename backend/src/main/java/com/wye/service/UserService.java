package com.wye.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wye.common.Result;
import com.wye.dto.LoginRequest;
import com.wye.dto.RegisterRequest;
import com.wye.entity.SysOwner;
import com.wye.entity.SysRepairWorker;
import com.wye.entity.SysUser;
import com.wye.entity.BusRepair;
import com.wye.entity.BusFee;
import com.wye.entity.BusForum;
import com.wye.mapper.SysOwnerMapper;
import com.wye.mapper.SysRepairWorkerMapper;
import com.wye.mapper.SysUserMapper;
import com.wye.mapper.BusRepairMapper;
import com.wye.mapper.BusFeeMapper;
import com.wye.mapper.BusForumMapper;
import com.wye.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRepairWorkerMapper sysRepairWorkerMapper;

    @Autowired
    private SysOwnerMapper sysOwnerMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BusRepairMapper busRepairMapper;

    @Autowired
    private BusFeeMapper busFeeMapper;

    @Autowired
    private BusForumMapper busForumMapper;
    
    /**
     * 用户登录
     */
    public Result<Map<String, Object>> login(LoginRequest request) {
        // 尝试通过用户名登录
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);
        
        // 如果通过用户名未找到用户，尝试通过手机号登录
        if (user == null) {
            wrapper.clear();
            wrapper.eq("phone", request.getUsername());
            user = sysUserMapper.selectOne(wrapper);
        }
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }
        
        // 验证角色
        if (!user.getRole().equals(request.getLoginType())) {
            return Result.error("角色不匹配");
        }
        
        // 如果是维修员，检查状态
        if (user.getRole() == 2) {
            SysRepairWorker worker = sysRepairWorkerMapper.selectById(user.getId());
            if (worker == null) {
                return Result.error("维修员信息不存在");
            }
            if (worker.getStatus() == 0) {
                return Result.error("账号待审核");
            }
            if (worker.getStatus() == 2) {
                return Result.error("账号已被禁用");
            }
        }
        
        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("phone", user.getPhone());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());
        
        return Result.success(data);
    }
    
    /**
     * 维修员注册
     */
    @Transactional
    public Result<String> registerWorker(RegisterRequest request) {
        // 检查用户名是否存在
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setRole(2); // 维修员
        sysUserMapper.insert(user);

        // 创建维修员扩展信息
        SysRepairWorker worker = new SysRepairWorker();
        worker.setUserId(user.getId());
        worker.setStatus(0); // 待审核
        sysRepairWorkerMapper.insert(worker);

        return Result.success("注册成功，请等待管理员审核");
    }

    /**
     * 业主注册
     */
    @Transactional
    public Result<String> registerOwner(RegisterRequest request) {
        // 检查用户名是否存在
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setRole(1); // 业主
        sysUserMapper.insert(user);

        // 创建业主扩展信息
        SysOwner owner = new SysOwner();
        owner.setUserId(user.getId());
        owner.setBuilding(request.getBuilding() != null ? request.getBuilding() : "");
        owner.setUnit(request.getUnit() != null ? request.getUnit() : "");
        owner.setRoom(request.getRoom() != null ? request.getRoom() : "");
        owner.setIdCard(request.getIdCard() != null ? request.getIdCard() : "");
        sysOwnerMapper.insert(owner);

        return Result.success("注册成功");
    }

    /**
     * 根据ID获取用户信息
     */
    public Result<?> getUserById(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("realName", user.getRealName());
        userMap.put("phone", user.getPhone());
        userMap.put("avatar", user.getAvatar());
        userMap.put("role", user.getRole());
        userMap.put("createTime", user.getCreateTime());
        
        // 如果是业主，添加房号和身份证号信息
        if (user.getRole() == 1) {
            SysOwner owner = sysOwnerMapper.selectByUserId(user.getId());
            if (owner != null) {
                userMap.put("building", owner.getBuilding());
                userMap.put("unit", owner.getUnit());
                userMap.put("room", owner.getRoom());
                userMap.put("idCard", owner.getIdCard());
            } else {
                userMap.put("building", "");
                userMap.put("unit", "");
                userMap.put("room", "");
                userMap.put("idCard", "");
            }
        }
        
        return Result.success(userMap);
    }

    /**
     * 获取所有业主列表
     */
    public Result<?> getOwners() {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("role", 1);
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        
        // 为每个业主添加房号信息
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysUser user : users) {
            Map<String, Object> ownerMap = new HashMap<>();
            ownerMap.put("id", user.getId());
            ownerMap.put("username", user.getUsername());
            ownerMap.put("realName", user.getRealName());
            ownerMap.put("phone", user.getPhone());
            ownerMap.put("avatar", user.getAvatar());
            ownerMap.put("role", user.getRole());
            ownerMap.put("createTime", user.getCreateTime());
            
            // 查询业主房号信息
            SysOwner owner = sysOwnerMapper.selectByUserId(user.getId());
            if (owner != null) {
                ownerMap.put("building", owner.getBuilding());
                ownerMap.put("unit", owner.getUnit());
                ownerMap.put("room", owner.getRoom());
                ownerMap.put("idCard", owner.getIdCard());
            } else {
                ownerMap.put("building", "");
                ownerMap.put("unit", "");
                ownerMap.put("room", "");
                ownerMap.put("idCard", "");
            }
            
            result.add(ownerMap);
        }
        
        return Result.success(result);
    }

    /**
     * 获取所有维修员列表
     */
    public Result<?> getWorkers() {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("role", 2);
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        
        // 为每个维修员添加status字段
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysUser user : users) {
            Map<String, Object> workerMap = new HashMap<>();
            workerMap.put("id", user.getId());
            workerMap.put("username", user.getUsername());
            workerMap.put("realName", user.getRealName());
            workerMap.put("phone", user.getPhone());
            workerMap.put("avatar", user.getAvatar());
            workerMap.put("role", user.getRole());
            workerMap.put("createTime", user.getCreateTime());
            
            // 查询维修员状态
            SysRepairWorker worker = sysRepairWorkerMapper.selectByUserId(user.getId());
            if (worker != null) {
                workerMap.put("status", worker.getStatus());
            } else {
                workerMap.put("status", 0); // 默认为待审核
            }
            
            result.add(workerMap);
        }
        
        return Result.success(result);
    }

    /**
     * 更新业主信息
     */
    @Transactional
    public Result<String> updateOwner(RegisterRequest request) {
        if (request.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        SysUser user = sysUserMapper.selectById(request.getUserId());
        if (user == null || user.getRole() != 1) {
            return Result.error("业主不存在");
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            if (!request.getUsername().equals(user.getUsername())) {
                QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
                wrapper.eq("username", request.getUsername());
                if (sysUserMapper.selectCount(wrapper) > 0) {
                    return Result.error("用户名已存在");
                }
            }
            user.setUsername(request.getUsername());
        }
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(request.getPassword()));
        }
        sysUserMapper.updateById(user);

        // 更新业主扩展信息
        SysOwner owner = sysOwnerMapper.selectByUserId(request.getUserId());
        if (owner != null) {
            if (request.getBuilding() != null) owner.setBuilding(request.getBuilding());
            if (request.getUnit() != null) owner.setUnit(request.getUnit());
            if (request.getRoom() != null) owner.setRoom(request.getRoom());
            if (request.getIdCard() != null) owner.setIdCard(request.getIdCard());
            sysOwnerMapper.updateById(owner);
        }

        return Result.success("更新成功");
    }

    /**
     * 审核维修员
     */
    public Result<String> approveWorker(Long workerId, Integer status) {
        SysUser user = sysUserMapper.selectById(workerId);
        if (user == null || user.getRole() != 2) {
            return Result.error("维修员不存在");
        }

        SysRepairWorker worker = sysRepairWorkerMapper.selectByUserId(workerId);
        if (worker == null) {
            return Result.error("维修员信息不存在");
        }

        worker.setStatus(status);
        sysRepairWorkerMapper.updateById(worker);

        return Result.success(status == 1 ? "审核通过" : "已禁用");
    }

    /**
     * 更新维修员信息
     */
    @Transactional
    public Result<String> updateWorker(RegisterRequest request) {
        if (request.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        SysUser user = sysUserMapper.selectById(request.getUserId());
        if (user == null || user.getRole() != 2) {
            return Result.error("维修员不存在");
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            if (!request.getUsername().equals(user.getUsername())) {
                QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
                wrapper.eq("username", request.getUsername());
                if (sysUserMapper.selectCount(wrapper) > 0) {
                    return Result.error("用户名已存在");
                }
            }
            user.setUsername(request.getUsername());
        }
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(request.getPassword()));
        }
        sysUserMapper.updateById(user);

        return Result.success("更新成功");
    }

    /**
     * 更新用户信息（根据角色自动选择）
     */
    public Result<String> updateUser(RegisterRequest request) {
        if (request.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        SysUser user = sysUserMapper.selectById(request.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 根据角色调用对应的更新方法
        if (user.getRole() == 1) {
            return updateOwner(request);
        } else if (user.getRole() == 2) {
            return updateWorker(request);
        } else if (user.getRole() == 0) {
            return updateAdmin(request);
        }

        return Result.error("角色未知");
    }

    /**
     * 更新管理员信息
     */
    public Result<String> updateAdmin(RegisterRequest request) {
        if (request.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        SysUser user = sysUserMapper.selectById(request.getUserId());
        if (user == null || user.getRole() != 0) {
            return Result.error("管理员不存在");
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            if (!request.getUsername().equals(user.getUsername())) {
                QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
                wrapper.eq("username", request.getUsername());
                if (sysUserMapper.selectCount(wrapper) > 0) {
                    return Result.error("用户名已存在");
                }
            }
            user.setUsername(request.getUsername());
        }
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(request.getPassword()));
        }
        sysUserMapper.updateById(user);

        return Result.success("更新成功");
    }

    /**
     * 删除用户
     */
    @Transactional
    public Result<String> deleteUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 删除扩展信息
        if (user.getRole() == 1) {
            SysOwner owner = sysOwnerMapper.selectByUserId(userId);
            if (owner != null) {
                sysOwnerMapper.deleteById(owner.getId());
            }
        } else if (user.getRole() == 2) {
            SysRepairWorker worker = sysRepairWorkerMapper.selectByUserId(userId);
            if (worker != null) {
                sysRepairWorkerMapper.deleteById(worker.getId());
            }
        }

        // 删除用户
        sysUserMapper.deleteById(userId);

        return Result.success("删除成功");
    }
    
    /**
     * 修改密码
     */
    public Result<String> changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return Result.error("旧密码错误");
        }
        
        // 更新密码
        user.setPassword(BCrypt.hashpw(newPassword));
        sysUserMapper.updateById(user);
        
        return Result.success("密码修改成功");
    }

    /**
     * 获取首页统计数据
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 业主总数
        QueryWrapper<SysUser> ownerWrapper = new QueryWrapper<>();
        ownerWrapper.eq("role", 1);
        stats.put("ownerCount", sysUserMapper.selectCount(ownerWrapper));

        // 待处理报修（状态为0或1的报修单）
        QueryWrapper<BusRepair> repairWrapper = new QueryWrapper<>();
        repairWrapper.in("status", 0, 1);
        stats.put("pendingRepairs", busRepairMapper.selectCount(repairWrapper));

        // 本月缴费
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String currentMonth = sdf.format(new Date());
        QueryWrapper<BusFee> feeWrapper = new QueryWrapper<>();
        feeWrapper.eq("month", currentMonth);
        feeWrapper.eq("status", 1);
        List<BusFee> fees = busFeeMapper.selectList(feeWrapper);
        double monthFee = 0;
        for (BusFee fee : fees) {
            if (fee != null && fee.getAmount() != null) {
                monthFee += fee.getAmount().doubleValue();
            }
        }
        stats.put("monthFee", monthFee);

        // 今日活跃（简化为所有已登录的业主）
        // 由于没有登录记录表，这里暂时返回业主总数的一部分作为模拟
        QueryWrapper<SysUser> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("role", 1);
        long totalOwners = sysUserMapper.selectCount(activeWrapper);
        // 假设40%的业主今日活跃
        stats.put("todayActive", (int) (totalOwners * 0.4));

        // 报修类型占比
        Map<String, Integer> repairTypes = new HashMap<>();
        repairTypes.put("水电维修", 0);
        repairTypes.put("家具维修", 0);
        repairTypes.put("电器维修", 0);
        repairTypes.put("管道疏通", 0);
        repairTypes.put("其他", 0);

        QueryWrapper<BusRepair> allRepairWrapper = new QueryWrapper<>();
        List<BusRepair> allRepairs = busRepairMapper.selectList(allRepairWrapper);

        for (BusRepair repair : allRepairs) {
            // 由于数据库可能没有type字段，使用content进行分析
            String content = repair.getContent();
            if (content != null) {
                if (content.contains("水") || content.contains("电")) {
                    repairTypes.put("水电维修", repairTypes.get("水电维修") + 1);
                } else if (content.contains("家具")) {
                    repairTypes.put("家具维修", repairTypes.get("家具维修") + 1);
                } else if (content.contains("电器") || content.contains("家电") || content.contains("空调") || content.contains("冰箱") || content.contains("洗衣机")) {
                    repairTypes.put("电器维修", repairTypes.get("电器维修") + 1);
                } else if (content.contains("管道") || content.contains("堵塞") || content.contains("疏通")) {
                    repairTypes.put("管道疏通", repairTypes.get("管道疏通") + 1);
                } else {
                    repairTypes.put("其他", repairTypes.get("其他") + 1);
                }
            } else {
                repairTypes.put("其他", repairTypes.get("其他") + 1);
            }
        }
        stats.put("repairTypes", repairTypes);

        // 近七日缴费趋势
        Map<String, Double> weekFeeTrend = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            Calendar cal = (Calendar) calendar.clone();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String date = dayFormat.format(cal.getTime());

        QueryWrapper<BusFee> dayFeeWrapper = new QueryWrapper<>();
        dayFeeWrapper.apply("DATE_FORMAT(pay_time, '%Y-%m-%d') = {0}", date);
        dayFeeWrapper.eq("status", 1);
        List<BusFee> dayFees = busFeeMapper.selectList(dayFeeWrapper);
        double dayTotal = 0;
        for (BusFee fee : dayFees) {
            if (fee != null && fee.getAmount() != null) {
                dayTotal += fee.getAmount().doubleValue();
            }
        }
            weekFeeTrend.put(date, dayTotal);
        }
        stats.put("weekFeeTrend", weekFeeTrend);

        // 近七日论坛发帖趋势
        Map<String, Integer> weekForumTrend = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            Calendar cal = (Calendar) calendar.clone();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String date = dayFormat.format(cal.getTime());

            QueryWrapper<BusForum> dayForumWrapper = new QueryWrapper<>();
            dayForumWrapper.apply("DATE_FORMAT(create_time, '%Y-%m-%d') = {0}", date);
            int dayCount = busForumMapper.selectCount(dayForumWrapper).intValue();
            weekForumTrend.put(date, dayCount);
        }
        stats.put("weekForumTrend", weekForumTrend);

        return stats;
    }

    /**
     * 管理员注册
     */
    @Transactional
    public Result<String> registerAdmin(RegisterRequest request) {
        // 检查用户名是否存在
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // 创建管理员用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setRole(0); // 管理员
        sysUserMapper.insert(user);

        return Result.success("管理员注册成功");
    }

    /**
     * 获取所有管理员列表
     */
    public Result<?> getAdmins() {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("role", 0);
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    /**
     * 添加管理员
     */
    @Transactional
    public Result<String> addAdmin(RegisterRequest request) {
        return registerAdmin(request);
    }



    /**
     * 删除管理员
     */
    @Transactional
    public Result<String> deleteAdmin(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getRole() != 0) {
            return Result.error("管理员不存在");
        }

        // 删除管理员
        sysUserMapper.deleteById(userId);

        return Result.success("删除成功");
    }
}
