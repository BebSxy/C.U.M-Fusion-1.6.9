
package me.cum.fusion.util;

public class PairUtil<F, S>
{
    private F first;
    private S second;
    
    public PairUtil(final F f, final S s) {
        this.first = f;
        this.second = s;
    }
    
    public F getFirst() {
        return this.first;
    }
    
    public void setFirst(final F f) {
        this.first = f;
    }
    
    public S getSecond() {
        return this.second;
    }
    
    public void setSecond(final S s) {
        this.second = s;
    }
}
