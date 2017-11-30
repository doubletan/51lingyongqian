package com.example.apple.lingyongqian.model;

/**
 * Created by apple on 2017/3/28.
 */

public class MessageBean {

    /**
     * Geren : {"username":"13811882233","password":"","userid":"158","channel":"Andriod-现金贷","qudao":"momo"}
     */

    private GerenBean Geren;

    public GerenBean getGeren() {
        return Geren;
    }

    public void setGeren(GerenBean Geren) {
        this.Geren = Geren;
    }

    public static class GerenBean {
        /**
         * username : 13811882233
         * password :
         * userid : 158
         * channel : Andriod-现金贷
         * qudao : momo
         */

        private String username;
        private String password;
        private String userid;
        private String channel;
        private String qudao;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getQudao() {
            return qudao;
        }

        public void setQudao(String qudao) {
            this.qudao = qudao;
        }

        @Override
        public String toString() {
            return "GerenBean{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", userid='" + userid + '\'' +
                    ", channel='" + channel + '\'' +
                    ", qudao='" + qudao + '\'' +
                    '}';
        }
    }
    @Override
    public String toString() {
        return "MessageBean{" +
                "Geren=" + Geren +
                '}';
    }
}
