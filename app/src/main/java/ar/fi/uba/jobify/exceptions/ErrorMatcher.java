package ar.fi.uba.jobify.exceptions;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by smpiano on 9/28/16.
 */
public enum ErrorMatcher {
    DEFAULT_ERROR(BusinessException.class),
    UNKNOWN(UnknownException.class),
    LOGFAILED(LoginException.class);

    private Class<? extends BusinessException> throwable;

    private ErrorMatcher(Class<? extends BusinessException> throwable) {
        this.throwable = throwable;
    }

    public BusinessException getThrowable(String msg, Integer status) {
        try {
            return throwable.getDeclaredConstructor(String.class, Integer.class).newInstance(msg, status);
        } catch (InstantiationException e) {
            Log.e("error_matcher","No se encuentra constructor por defecto para "+throwable.getName(),e);
        } catch (IllegalAccessException e) {
            Log.e("error_matcher", "No se puede instanciar " + throwable.getName(), e);
        } catch (NoSuchMethodException e) {
            Log.e("error_matcher", "No se encuentra el metodo con esos params " + throwable.getName(), e);
        } catch (InvocationTargetException e) {
            Log.e("error_matcher", "Mala invocacion " + throwable.getName(), e);
        }
        return null;
    }
}
