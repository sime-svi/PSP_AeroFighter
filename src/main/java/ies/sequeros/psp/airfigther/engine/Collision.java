package ies.sequeros.psp.airfigther.engine;

import ies.sequeros.psp.airfigther.model.Element;

public class Collision {
    private final Element one;
    private final Element two;

    public Collision(Element one, Element two) {
        this.one = one;
        this.two = two;

    }
    public Element getOne() {
        return one;
    }
    public Element getTwo() {
        return two;
    }

}
