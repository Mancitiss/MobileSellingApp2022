package org.duckdns.mancitiss.buyfoodserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Mancitiss
 */
public class Client {

    SSLSocket client;
    DataOutputStream stream;
    DataInputStream DIS;
    String token;
    AtomicReference<Integer> is_locked = new AtomicReference<>(0);
    AtomicReference<Integer> is_waited = new AtomicReference<>(0);
    ConcurrentLinkedQueue<byte[]> commands;
    private AtomicReference<Integer> workeradded = new AtomicReference<>(0);

    Client() {
        commands = new ConcurrentLinkedQueue<byte[]>();
        workeradded.set(0);
        is_waited.set(0);
    }

    void Queue_command(byte[] command) {
        commands.add(command);
        Add_worker_thread();
    }

    private void Add_worker_thread() {
        if (0 == workeradded.getAndSet(1)) {
            Runnable worker = new Send_commands();
            ServerMain.executor.execute(worker);
        }
    }

    private class Send_commands implements Runnable {

        Send_commands() {
        }

        @Override
        public void run() {
            try
            {
                while (commands.size() > 0)
                {
                    byte[] command = commands.poll();
                    stream.write(command);
                    stream.flush();
                }
            }
            catch (Exception se)
            {
                se.printStackTrace();
                ServerMain.shutdown(token);
            }
            finally
            {
                try
                {
                    workeradded.set(0);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}