/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package pl.edu.agh.icsr.janet.yytree;

import java.util.Iterator;

import pl.edu.agh.icsr.janet.IJavaContext;
import pl.edu.agh.icsr.janet.ParseException;
import pl.edu.agh.icsr.janet.natives.IWriter;
import pl.edu.agh.icsr.janet.reflect.IClassInfo;

public class YYArrayAccessExpression extends YYExpression {

    YYExpression target;
    YYExpression dimexpr;

    public YYArrayAccessExpression(IJavaContext cxt, YYExpression target,
                                   YYExpression dimexpr) {
        super(cxt);
        this.target = target;
        this.dimexpr = dimexpr;
    }

    public void resolve(boolean isSubexpression) throws ParseException {
        target.resolve(true);
        dimexpr.resolve(true);

        IClassInfo reftype = target.getExpressionType();
        IClassInfo dimtype = dimexpr.getExpressionType();
        if (!reftype.isArray()) {
            reportError(reftype.toString() + " is not an array type");
        }
        if (dimtype != classMgr.NATIVETYPE && !dimtype.isAssignableFrom(classMgr.INT)) {
            reportError(dimtype.toString() + " cannot be converted to int");
        }

        //OK
        dimexpr.setImplicitCastType(classMgr.INT);
        expressionType = reftype.getComponentType();

        addExceptions(target.getExceptionsThrown());
        addExceptions(dimexpr.getExceptionsThrown());
        addException(classMgr.NullPointerException);
        addException(classMgr.ArrayIndexOutOfBoundsException);

        if (expressionType.isPrimitive()) {
            findImpl().addReferencedPrimitiveTypeArray();
        }
    }

    public YYExpression getTarget() { return target; }
    public YYExpression getIndexExpression() { return dimexpr; }

    public boolean isVariable() { return true; }

    public int write(IWriter w, int param) throws java.io.IOException {
        return w.write(this, param);
    }

    class DumpIterator implements Iterator<YYNode> {
        int i = 0;
        DumpIterator() {}
        public boolean hasNext() { return i<2; }
        public YYNode next() {
            i++;
            return (i == 1 ? target : i == 2 ? dimexpr : null);
        }
        public void remove() { throw new UnsupportedOperationException(); }
    }

    public Iterator<YYNode> getDumpIterator() { return new DumpIterator(); }

}