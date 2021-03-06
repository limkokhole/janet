/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package pl.edu.agh.icsr.janet.yytree;

import java.util.Iterator;

import pl.edu.agh.icsr.janet.IJavaContext;
import pl.edu.agh.icsr.janet.ParseException;
import pl.edu.agh.icsr.janet.natives.IWriter;
import pl.edu.agh.icsr.janet.natives.YYNativeCode;

public class YYEnclosedNativeString extends YYExpression {

    boolean unicode;
    YYNativeCode ncode;

    public YYEnclosedNativeString(IJavaContext cxt, YYNativeCode ncode) {
        this(cxt, ncode, false);
    }

    public YYEnclosedNativeString(IJavaContext cxt, YYNativeCode ncode,
                                  boolean unicode) {
        super(cxt);
        this.unicode = unicode;
        this.ncode = ncode;
    }

    public void resolve(boolean isSubexpression) throws ParseException {
        expressionType = classMgr.String;
        ncode.resolve();
        addExceptions(ncode.getExceptionsThrown());
        addException(classMgr.NullPointerException);
    }

    public boolean isVariable() { return false; }

    public boolean isUnicode() { return unicode; }

    public YYNativeCode getNativeString() { return ncode; }

    public int write(IWriter w, int param) throws java.io.IOException {
        return w.write(this, param);
    }




    class DumpIterator implements Iterator<YYNode> {
        boolean ncodereturned;
        public boolean hasNext() { return !ncodereturned; }
        public YYNode next() {
            if (!ncodereturned) { ncodereturned = true; return ncode; }
            return null;
        }
        public void remove() { throw new UnsupportedOperationException(); }
    }

    public Iterator<YYNode> getDumpIterator() { return new DumpIterator(); }

}