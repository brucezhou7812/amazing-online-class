package nz.co.component;

import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RabbitRequestAttributes implements RequestAttributes {
    private Map<String,Object> map = new ConcurrentHashMap<>();
    @Override
    public Object getAttribute(String name, int scope) {
        return map.get(name);
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        map.put(name,value);
    }

    @Override
    public void removeAttribute(String name, int scope) {
        map.remove(name);
    }

    @Override
    public String[] getAttributeNames(int scope) {
        return new String[0];
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {

    }

    @Override
    public Object resolveReference(String key) {
        return null;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public Object getSessionMutex() {
        return null;
    }
}
