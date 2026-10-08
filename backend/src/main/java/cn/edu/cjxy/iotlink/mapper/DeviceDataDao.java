package cn.edu.cjxy.iotlink.mapper;

import cn.edu.cjxy.iotlink.model.SysDevData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DeviceDataDao extends BaseMapper<SysDevData> {
    List<SysDevData> getDeviceDataList(Long deviceId);

    List<SysDevData> selectByCode(String projectCode);

    void updatePront(@Param("pront") String pront,@Param("code") String code);

    void updateIPC(@Param("id") Long id, @Param("point") String point, @Param("code") String code);

    boolean updateByICP(@Param("id") Long id, @Param("code") String code, @Param("point") String point,@Param("time") String time, @Param("dataName") String dataName, @Param("dataValue") String dataValue);

    int addPoint(@Param("id") Long id,@Param("point") String point,@Param("code") String code);

    int deletePoint(@Param("point") String point, @Param("code") String code);

    List<SysDevData> selectByIC(@Param("id") Long id,@Param("code") String code);
}
