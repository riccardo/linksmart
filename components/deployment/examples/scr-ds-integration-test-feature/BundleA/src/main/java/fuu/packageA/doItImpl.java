package fuu.packageA;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * Created with IntelliJ IDEA.
 * User: carlos
 * Date: 06.03.14
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
@Component(immediate=true)
@Service
public class doItImpl implements doIt {
    @Override
    public String doSomething(int A) {
        return String.valueOf(A);
    }
}
