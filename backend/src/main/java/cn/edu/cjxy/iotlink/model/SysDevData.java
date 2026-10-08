package cn.edu.cjxy.iotlink.model;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dev_data")
public class SysDevData {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("device_id")
    private Long deviceId;
    @TableField("task_id")
    private Long taskId;
    @TableField("collect_time")
    private String collectTime;
    @TableField("data_name")
    private String dataName;
    @TableField("data_value")
    private String dataValue;
    @TableField("project_code")
    private String projectCode;
    @TableField("acquisition_point")
    private String acquisitionPoint;
}
