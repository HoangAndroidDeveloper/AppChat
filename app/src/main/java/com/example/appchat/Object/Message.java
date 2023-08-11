package com.example.appchat.Object;

import java.util.List;

public class Message
{
    private Friend friend; // gửi qua để set dữ liệu cho user trong đoạn chat
    private List<TN> message;

    public Message() {
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public List<TN> getMessage() {
        return message;
    }

    public void setMessage(List<TN> message) {
        this.message = message;
    }


    public Message(Friend friend, List<TN> message) {
        this.friend = friend;
        this.message = message;
    }

    public static class TN
    {
        private String tnden, tndi, timeden,timedi;
        private String emojiden, emojidi;

        public TN() {

        }

        public TN(String tnden, String tndi, String timeden, String timedi, String emojiden, String emojidi) {
            this.tnden = tnden;
            this.tndi = tndi;
            this.timeden = timeden;
            this.timedi = timedi;
            this.emojiden = emojiden;
            this.emojidi = emojidi;
        }

        public String getTnden() {
            return tnden;
        }

        public void setTnden(String tnden) {
            this.tnden = tnden;
        }

        public String getTndi() {
            return tndi;
        }

        public void setTndi(String tndi) {
            this.tndi = tndi;
        }

        public String getTimeden() {
            return timeden;
        }

        public void setTimeden(String timeden) {
            this.timeden = timeden;
        }

        public String getTimedi() {
            return timedi;
        }

        public void setTimedi(String timedi) {
            this.timedi = timedi;
        }

        public String getEmojiden() {
            return emojiden;
        }

        public void setEmojiden(String emojiden) {
            this.emojiden = emojiden;
        }

        public String getEmojidi() {
            return emojidi;
        }

        public void setEmojidi(String emojidi) {
            this.emojidi = emojidi;
        }
    }
}
