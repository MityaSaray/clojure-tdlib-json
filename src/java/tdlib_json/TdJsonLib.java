package tdlib_json;


import com.sun.jna.Library;
import com.sun.jna.Pointer;


interface TdJsonLib extends Library {
    Pointer td_json_client_create();

    void td_json_client_send(Pointer pointer, String message);

    String td_json_client_receive(Pointer pointer, Double timeout);

    String td_json_client_execute(Pointer pointer, String command);

    void td_json_client_destroy(Pointer pointer);

    void td_set_log_verbosity_level(Integer level);
}
