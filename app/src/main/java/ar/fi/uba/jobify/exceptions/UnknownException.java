package ar.fi.uba.jobify.exceptions;

/**
 * Created by smpiano on 9/28/16.
 */
public class UnknownException extends BusinessException {

    public UnknownException(String msg, Throwable e) {
        super("No se sabe el motivo", e);
    }

    public UnknownException(String msg, Integer status) {
        super("No se sabe el motivo [status:"+status+"] - " + msg);
    }
}
