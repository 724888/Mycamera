package com.example.jiexingxing.mycamera.camera.scan;

import java.io.Serializable;

/**
 * Created by jiexingxing on 2017/8/7.
 */

public class CameraResultBean implements Serializable {



    private String status_code;
    private Message message;



    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


    public static class Message implements Serializable {
        private String owner;
       private String plateNum;// 车牌号码
        private String type;// 车辆类型
        private String vin;// 车辆识别代号
        private String engineNum;// 发动机代号
        private String registerDate;// 注册日期

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getPlateNum() {
            return plateNum;
        }

        public void setPlateNum(String plateNum) {
            this.plateNum = plateNum;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public String getEngineNum() {
            return engineNum;
        }

        public void setEngineNum(String engineNum) {
            this.engineNum = engineNum;
        }

        public String getRegisterDate() {
            return registerDate;
        }

        public void setRegisterDate(String registerDate) {
            this.registerDate = registerDate;
        }

    }

}
