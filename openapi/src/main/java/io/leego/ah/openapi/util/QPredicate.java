package io.leego.ah.openapi.util;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A predicate builder for Querydsl.
 *
 * @author Leego Yih
 */
public class QPredicate implements Predicate {
    /** Current predicate */
    private Predicate predicate;

    /**
     * Constructs an empty predicate builder.
     */
    public QPredicate() {
    }

    /**
     * Constructs a predicate builder with the given initial value.
     *
     * @param initial the initial value
     */
    public QPredicate(Predicate initial) {
        predicate = (Predicate) ExpressionUtils.extract(initial);
    }

    /**
     * Creates an empty predicate builder.
     */
    public static QPredicate create() {
        return new QPredicate();
    }

    /**
     * Creates a predicate builder with the given initial value.
     *
     * @param initial the initial value
     */
    public static QPredicate create(Predicate initial) {
        return new QPredicate(initial);
    }

    /**
     * Accepts the visitor with the given context.
     *
     * @param v       the visitor
     * @param context the context of visit
     * @return the result of visit
     */
    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return predicate != null ? predicate.accept(v, context) : null;
    }

    /**
     * Returns the java type for this expression.
     *
     * @return the type of expression
     */
    @Override
    public Class<? extends Boolean> getType() {
        return Boolean.class;
    }

    /**
     * Returns the negation of the expression.
     *
     * @return the negation of the expression
     */
    public QPredicate not() {
        if (predicate != null) {
            predicate = predicate.not();
        }
        return this;
    }

    /**
     * Creates the intersection of this and the given predicate.
     *
     * @param right right hand side of {@code and} operation
     * @return the current object
     */
    public QPredicate and(Predicate right) {
        if (right != null) {
            predicate = ExpressionUtils.and(predicate, right);
        }
        return this;
    }

    /**
     * Creates the insertion of this and the negation of the given predicate.
     *
     * @param right predicate to be negated
     * @return the current object
     */
    public QPredicate andNot(Predicate right) {
        if (right != null) {
            and(right.not());
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given args.
     * <p>
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAllOf(Predicate... args) {
        if (args != null && args.length > 0) {
            and(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given args.
     * <p>
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAllOf(List<Predicate> args) {
        if (args != null && args.size() > 0) {
            and(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given args.
     * <p>
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAnyOf(Predicate... args) {
        if (args != null && args.length > 0) {
            and(ExpressionUtils.anyOf(args));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given args.
     * <p>
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAnyOf(List<Predicate> args) {
        if (args != null && args.size() > 0) {
            and(ExpressionUtils.anyOf(args));
        }
        return this;
    }

    /**
     * Creates the union of this and the given predicate
     *
     * @param right right hand side of {@code or} operation
     * @return the current object
     */
    public QPredicate or(Predicate right) {
        predicate = ExpressionUtils.or(predicate, right);
        return this;
    }

    /**
     * Creates the union of this and the negation of the given predicate.
     *
     * @param right predicate to be negated
     * @return the current object
     */
    public QPredicate orNot(Predicate right) {
        if (right != null) {
            or(right.not());
        }
        return this;
    }

    /**
     * Creates the union of this and the intersection of the given args
     * <p>
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param args intersection of predicates
     * @return the current object
     */
    public QPredicate orAllOf(Predicate... args) {
        if (args != null && args.length > 0) {
            or(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Creates the union of this and the intersection of the given args
     * <p>
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param args intersection of predicates
     * @return the current object
     */
    public QPredicate orAllOf(List<Predicate> args) {
        if (args != null && args.size() > 0) {
            or(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the given operation and value.
     * <p>
     * {@code (this && arg)}
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate and(Function<V, Predicate> operation, V value) {
        if (value != null && operation != null) {
            and(operation.apply(value));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the given operation and values.
     *
     * @param operation the operation
     * @param value1    the first value
     * @param value2    the second value
     * @return the current object
     */
    public <V1, V2> QPredicate and(BiFunction<V1, V2, Predicate> operation, V1 value1, V2 value2) {
        if ((value1 != null || value2 != null) && operation != null) {
            and(operation.apply(value1, value2));
        }
        return this;
    }

    /**
     * Creates the insertion of this and the negation of the given operation and value.
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate andNot(Function<V, Predicate> operation, V value) {
        if (value != null && operation != null) {
            andNot(operation.apply(value));
        }
        return this;
    }

    /**
     * Creates the insertion of this and the negation of the given operation and values.
     *
     * @param operation the operation
     * @param value1    the first value
     * @param value2    the second value
     * @return the current object
     */
    public <V1, V2> QPredicate andNot(BiFunction<V1, V2, Predicate> operation, V1 value1, V2 value2) {
        if ((value1 != null || value2 != null) && operation != null) {
            andNot(operation.apply(value1, value2));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given operation and values.
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAllOf(Function<V, Predicate> operation, V... values) {
        if (values != null && values.length > 0 && operation != null) {
            andAllOf(Arrays.stream(values).filter(Objects::nonNull).map(operation).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given operation and values.
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAllOf(Function<V, Predicate> operation, Collection<V> values) {
        if (values != null && values.size() > 0 && operation != null) {
            andAllOf(values.stream().filter(Objects::nonNull).map(operation).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given operation and values.
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAnyOf(Function<V, Predicate> operation, V... values) {
        if (values != null && values.length > 0 && operation != null) {
            andAnyOf(Arrays.stream(values).filter(Objects::nonNull).map(operation).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Creates the intersection of this and the union of the given operation and values.
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAnyOf(Function<V, Predicate> operation, Collection<V> values) {
        if (values != null && values.size() > 0 && operation != null) {
            andAnyOf(values.stream().filter(Objects::nonNull).map(operation).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Creates the union of this and the given operation and value.
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate or(Function<V, Predicate> operation, V value) {
        if (value != null && operation != null) {
            or(operation.apply(value));
        }
        return this;
    }

    /**
     * Creates the union of this and the given operation and values.
     *
     * @param operation the operation
     * @param value1    the first value
     * @param value2    the second value
     * @return the current object
     */
    public <V1, V2> QPredicate or(BiFunction<V1, V2, Predicate> operation, V1 value1, V2 value2) {
        if ((value1 != null || value2 != null) && operation != null) {
            or(operation.apply(value1, value2));
        }
        return this;
    }

    /**
     * Creates the union of this and the negation of the given operation and value.
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate orNot(Function<V, Predicate> operation, V value) {
        if (value != null && operation != null) {
            orNot(operation.apply(value));
        }
        return this;
    }

    /**
     * Creates the union of this and the negation of the given operation and values.
     *
     * @param operation the operation
     * @param value1    the first value
     * @param value2    the second value
     * @return the current object
     */
    public <V1, V2> QPredicate orNot(BiFunction<V1, V2, Predicate> operation, V1 value1, V2 value2) {
        if ((value1 != null || value2 != null) && operation != null) {
            orNot(operation.apply(value1, value2));
        }
        return this;
    }

    /**
     * Creates the union of this and the intersection of the given operation and values.
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate orAllOf(Function<V, Predicate> operation, V... values) {
        if (values != null && values.length > 0 && operation != null) {
            orAllOf(Arrays.stream(values).filter(Objects::nonNull).map(operation).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Creates the union of this and the intersection of the given operation and values.
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate orAllOf(Function<V, Predicate> operation, Collection<V> values) {
        if (values != null && values.size() > 0 && operation != null) {
            orAllOf(values.stream().filter(Objects::nonNull).map(operation).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Returns {@code true} if the value is set, and false, if not.
     *
     * @return true if initialized and false if not
     */
    public boolean isPresent() {
        return predicate != null;
    }

    /**
     * Returns {@code true} if the value is null, and false, if not.
     *
     * @return true if absent and false if not
     */
    public boolean isEmpty() {
        return predicate == null;
    }

    public Optional<Predicate> optional() {
        return Optional.ofNullable(predicate);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof QPredicate) {
            return Objects.equals(((QPredicate) o).predicate, predicate);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return predicate != null ? predicate.hashCode() : 0;
    }

    @Override
    public String toString() {
        return predicate != null ? predicate.toString() : super.toString();
    }
}
