package main.java;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.Objects;

public class TgClient {
    private static String pathToLib;
    private static Pointer tgInstancePointer;
    private static String errorMessage = "Tdlib client was null";
    private TdLibrary client;
    private double timeout;

    public TgClient(String path, double timeout) {
        pathToLib = path;
        this.timeout = timeout;
    }

    public void startClient(Integer verbosityLevel) {
        if (tgInstancePointer == null) {
            client = TdLibrary.INSTANCE;
            tgInstancePointer = client.td_json_client_create();
            client.td_set_log_verbosity_level(Objects.requireNonNullElse(verbosityLevel, 2));
        }
    }

    public void send(String message, boolean logout) {
        if (tgInstancePointer != null) {
            client.td_json_client_send(tgInstancePointer, message);
            if (logout) {
                tgInstancePointer = null;
            }
        } else {
            throw new Error(errorMessage);
        }
    }

    public String receive() {
        if (tgInstancePointer != null) {
            return client.td_json_client_receive(tgInstancePointer, this.timeout);
        } else {
            throw new Error(errorMessage);
        }
    }

    public String execute(String command) {
        return client.td_json_client_execute(tgInstancePointer, command);
    }

    public boolean destroyClient() {
        if (tgInstancePointer != null) {
            client.td_json_client_destroy(tgInstancePointer);
            tgInstancePointer = null;
            return true;
        }
        return false;
    }


    public interface TdLibrary extends Library {
        TdLibrary INSTANCE = (TdLibrary)
                Native.loadLibrary(pathToLib,
                        TdLibrary.class);

        Pointer td_json_client_create();

        String td_json_client_receive(Pointer pointer, double timeout);

        String td_json_client_execute(Pointer pointer, String command);

        void td_json_client_destroy(Pointer pointer);

        void td_json_client_send(Pointer pointer, String message);

        void td_set_log_verbosity_level(int level);
    }
}
