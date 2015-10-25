package org.newdeal.core.engine;

import org.newdeal.core.bean.BeanType;

import java.util.Optional;

/**
 * -- UNSTABLE --
 *
 * For searching purpose only
 *
 * @author Frederick Vanderbeke
 * @since 23/06/2015
 * @version 0.0.1
 */
public class PartialEngine extends AbstractEngine {

    private final BeanType beanType;

    private final String name;

    public PartialEngine(BeanType beanType, String name) {
        super(name, "", null);
        this.beanType = beanType;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public BeanType getType() {
        return beanType;
    }

    @Override
    public Optional<String> getComment() {
        return Optional.empty();
    }
}
