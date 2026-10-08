package cn.edu.cjxy.iotlink.model;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_device")
public class SysDevice {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("name")
    private String name;
    @TableField("type")
    private String type;
    @TableField("node_id")
    private String nodeId;
    @TableField("dev_address")
    private String devAddress;
    @TableField("func_code")
    private String funcCode;
    @TableField("reg_bytes")
    private Long regBytes;
    @TableField("reg_address")
    private String regAddress;
    @TableField("data_bytes")
    private Long dataBytes;
    @TableField("data_len")
    private String dataLen;
    @TableField("data_start")
    private String dataStart;
    @TableField("data_stop")
    private String dataStop;
    @TableField("crc_mode")
    private String crcMode;
    @TableField("collect_cmd")
    private String collectCmd;
    @TableField("start_cmd")
    private String startCmd;
    @TableField("stop_cmd")
    private String stopCmd;
    @TableField("threshold")
    private String threshold;
    @TableField("data_name")
    private String dataName;
    @TableField("data_range")
    private String dataRange;
    @TableField("alarm")
    private String alarm;
    @TableField("alarm_status")
    private String alarmStatus;
    @TableField("status")
    private String status;
    @TableField("create_time")
    private String createTime;
    @TableField(exist = false)
    private String dataValue;
    @TableField(exist = false)
    private String projectName;
    @TableField("user_id")
    private int userId;
    @TableField("user")
    private String user;
    @TableField("project_code")
    private String projectCode;
    @TableField("acquisition_point")
    private String acquisitionPoint;
}
