package org.duckdns.mancitiss.buyfoodserver;

public class Receive_message implements Runnable {
    String ID;

    public Receive_message(String data) {
        this.ID = data;
    }

    @Override
    public void run() {
        try {
            java.io.PushbackInputStream s = new java.io.PushbackInputStream(ServerMain.sessions.get(ID).DIS);
            String data;
            boolean keepreading = true;
            do {
                data = Tools.receive_unicode(s, 8);

                if (data != null && !data.isBlank()) {
                    //if (data!=null && data!="1904") System.out.println("Work: " + data);
                    // if (!data.equals("1904"))

                    String instruction = data;
                    switch (instruction){
                        default:{
                            System.out.println("Unknown instruction: " + instruction);
                            break;
                        }
                    }
                } else {
                    keepreading = false;
                    ServerMain.shutdown(ID);
                }
                int b = s.read();
                if (b == -1)
                    keepreading = false;
                else
                    s.unread(b);
            } while (keepreading);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                ServerMain.handleException(this.ID, e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {

                if (ServerMain.sessions.containsKey(this.ID)) {
                    ServerMain.sessions.get(this.ID).is_locked.set(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.flush();
        }
    }
}
