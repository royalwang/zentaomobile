package com.cnezsoft.zentao.data;

/**
 * DAO operate info
 * Created by Catouse on 2015/1/28.
 */
public class DAOOperateInfo {
    int addCount = 0;
    int deleteCount = 0;
    int updateCount = 0;
    int correctCount = 0;

    public int correct(int count) {
        correctCount += count;
        return correctCount;
    }

    public int add(int count) {
        addCount += count;
        return addCount;
    }

    public int delete(int count) {
        deleteCount += count;
        return deleteCount;
    }

    public int update(int count) {
        updateCount += count;
        return updateCount;
    }

    public int correct() {
        return correctCount;
    }

    public int add() {
        return addCount;
    }

    public int delete() {
        return deleteCount;
    }

    public int update() {
        return updateCount;
    }

    public int sum() {
        return addCount + deleteCount + updateCount;
    }

    public boolean notEmpty() {
        return sum() > 0;
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * object. Subclasses are encouraged to override this method and provide an
     * implementation that takes into account the object's type and data. The
     * default implementation is equivalent to the following expression:
     * <pre>
     *   getClass().getName() + '@' + Integer.toHexString(hashCode())</pre>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_toString">Writing a useful
     * {@code toString} method</a>
     * if you intend implementing your own {@code toString} method.
     *
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "{+" + add() + ", -" + delete() + ", *" + update() + ", $" + correct() + ", #" + sum() + "}";
    }
}
