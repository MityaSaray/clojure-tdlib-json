package tdlib_json;


import java.util.Objects;

import com.sun.jna.Native;
import com.sun.jna.Pointer;


public class TgJsonClient {
    private static String exceptionMessage = "Client is destroyed";

    private TdJsonLib lib;
    private Pointer client;

    public TgJsonClient(String pathToLib, Integer verbosityLevel) {
        lib = (TdJsonLib) Native.loadLibrary(pathToLib, TdJsonLib.class);
        client = lib.td_json_client_create();
        lib.td_set_log_verbosity_level(Objects.requireNonNullElse(verbosityLevel, 2));
    }

    public void send(String message) {
        if (client == null) throw new IllegalStateException(exceptionMessage);

        lib.td_json_client_send(client, message);
    }

    public String receive(Double timeout) {
        if (client == null) throw new IllegalStateException(exceptionMessage);

        return lib.td_json_client_receive(client, timeout);
    }

    public String execute(String command) {
        if (client == null) throw new IllegalStateException(exceptionMessage);

        return lib.td_json_client_execute(client, command);
    }

    public void destroy() {
        if (client == null) throw new IllegalStateException(exceptionMessage);

        lib.td_json_client_destroy(client);
        client = null;
    }
}
