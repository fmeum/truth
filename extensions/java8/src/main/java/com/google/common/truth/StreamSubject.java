/*
 * Copyright (c) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.truth;

import static java.util.stream.Collectors.toCollection;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Propositions for {@link Stream} subjects.
 *
 * <p><b>Note:</b> the wrapped stream will be drained immediately into a private collection to
 * provide more readable failure messages. You should not use this class if you intend to leave the
 * stream un-consumed or if the stream is <i>very</i> large or infinite.
 *
 * <p>If you intend to make multiple assertions on the same stream of data you should instead first
 * collect the contents of the stream into a collection, and then assert directly on that.
 *
 * <p>For very large or infinite streams you may want to first {@linkplain Stream#limit limit} the
 * stream before asserting on it.
 *
 * @author Kurt Alfred Kluever
 */
public final class StreamSubject extends Subject {

  private final List<?> actualList;

  private StreamSubject(FailureMetadata failureMetadata, @Nullable Stream<?> stream) {
    super(failureMetadata, stream);
    this.actualList = (stream == null) ? null : stream.collect(toCollection(ArrayList::new));
  }

  @Override
  protected String actualCustomStringRepresentation() {
    return String.valueOf(actualList);
  }

  public static Subject.Factory<StreamSubject, Stream<?>> streams() {
    return (metadata, subject) -> new StreamSubject(metadata, subject);
  }

  /** Fails if the subject is not empty. */
  public void isEmpty() {
    check().that(actualList).isEmpty();
  }

  /** Fails if the subject is empty. */
  public void isNotEmpty() {
    check().that(actualList).isNotEmpty();
  }

  /**
   * Fails if the subject does not have the given size.
   *
   * <p>If you'd like to check that your stream contains more than {@link Integer#MAX_VALUE}
   * elements, use {@code assertThat(stream.count()).isEqualTo(...)}.
   */
  public void hasSize(int expectedSize) {
    check().that(actualList).hasSize(expectedSize);
  }

  /** Fails if the subject does not contain the given element. */
  public void contains(@Nullable Object element) {
    check().that(actualList).contains(element);
  }

  /** Fails if the subject contains the given element. */
  public void doesNotContain(@Nullable Object element) {
    check().that(actualList).doesNotContain(element);
  }

  /** Fails if the subject contains duplicate elements. */
  public void containsNoDuplicates() {
    check().that(actualList).containsNoDuplicates();
  }

  /** Fails if the subject does not contain at least one of the given elements. */
  public void containsAnyOf(
      @Nullable Object first, @Nullable Object second, @Nullable Object @Nullable ... rest) {
    check().that(actualList).containsAnyOf(first, second, rest);
  }

  /** Fails if the subject does not contain at least one of the given elements. */
  public void containsAnyIn(Iterable<?> expected) {
    check().that(actualList).containsAnyIn(expected);
  }

  /**
   * Fails if the subject does not contain all of the given elements. If an element appears more
   * than once in the given elements, then it must appear at least that number of times in the
   * actual elements.
   *
   * <p>To also test that the contents appear in the given order, make a call to {@code inOrder()}
   * on the object returned by this method. The expected elements must appear in the given order
   * within the actual elements, but they are not required to be consecutive.
   */
  @CanIgnoreReturnValue
  public Ordered containsAtLeast(
      @Nullable Object first, @Nullable Object second, @Nullable Object @Nullable ... rest) {
    return check().that(actualList).containsAtLeast(first, second, rest);
  }

  /**
   * Fails if the subject does not contain all of the given elements. If an element appears more
   * than once in the given elements, then it must appear at least that number of times in the
   * actual elements.
   *
   * <p>To also test that the contents appear in the given order, make a call to {@code inOrder()}
   * on the object returned by this method. The expected elements must appear in the given order
   * within the actual elements, but they are not required to be consecutive.
   */
  @CanIgnoreReturnValue
  public Ordered containsAtLeastElementsIn(Iterable<?> expected) {
    return check().that(actualList).containsAtLeastElementsIn(expected);
  }

  // TODO(cpovirk): Add array overload of contains*ElementsIn methods? Also for int and long stream.

  /**
   * Fails if the subject does not contain exactly the given elements.
   *
   * <p>Multiplicity is respected. For example, an object duplicated exactly 3 times in the
   * parameters asserts that the object must likewise be duplicated exactly 3 times in the subject.
   *
   * <p>To also test that the contents appear in the given order, make a call to {@code inOrder()}
   * on the object returned by this method.
   */
  @CanIgnoreReturnValue
  public Ordered containsExactly(@Nullable Object @Nullable ... varargs) {
    return check().that(actualList).containsExactly(varargs);
  }

  /**
   * Fails if the subject does not contain exactly the given elements.
   *
   * <p>Multiplicity is respected. For example, an object duplicated exactly 3 times in the
   * parameters asserts that the object must likewise be duplicated exactly 3 times in the subject.
   *
   * <p>To also test that the contents appear in the given order, make a call to {@code inOrder()}
   * on the object returned by this method.
   */
  @CanIgnoreReturnValue
  public Ordered containsExactlyElementsIn(Iterable<?> expected) {
    return check().that(actualList).containsExactlyElementsIn(expected);
  }

  /**
   * Fails if the subject contains any of the given elements. (Duplicates are irrelevant to this
   * test, which fails if any of the actual elements equal any of the excluded.)
   */
  public void containsNoneOf(
      @Nullable Object first, @Nullable Object second, @Nullable Object @Nullable ... rest) {
    check().that(actualList).containsNoneOf(first, second, rest);
  }

  /**
   * Fails if the subject contains any of the given elements. (Duplicates are irrelevant to this
   * test, which fails if any of the actual elements equal any of the excluded.)
   */
  public void containsNoneIn(Iterable<?> excluded) {
    check().that(actualList).containsNoneIn(excluded);
  }

  /**
   * Fails if the subject is not strictly ordered, according to the natural ordering of its
   * elements. Strictly ordered means that each element in the stream is <i>strictly</i> greater
   * than the element that preceded it.
   *
   * @throws ClassCastException if any pair of elements is not mutually Comparable
   * @throws NullPointerException if any element is null
   */
  public void isInStrictOrder() {
    check().that(actualList).isInStrictOrder();
  }

  /**
   * Fails if the subject is not strictly ordered, according to the given comparator. Strictly
   * ordered means that each element in the stream is <i>strictly</i> greater than the element that
   * preceded it.
   *
   * @throws ClassCastException if any pair of elements is not mutually Comparable
   */
  public void isInStrictOrder(Comparator<?> comparator) {
    check().that(actualList).isInStrictOrder(comparator);
  }

  /**
   * Fails if the subject is not ordered, according to the natural ordering of its elements. Ordered
   * means that each element in the stream is greater than or equal to the element that preceded it.
   *
   * @throws ClassCastException if any pair of elements is not mutually Comparable
   * @throws NullPointerException if any element is null
   */
  public void isInOrder() {
    check().that(actualList).isInOrder();
  }

  /**
   * Fails if the subject is not ordered, according to the given comparator. Ordered means that each
   * element in the stream is greater than or equal to the element that preceded it.
   *
   * @throws ClassCastException if any pair of elements is not mutually Comparable
   */
  public void isInOrder(Comparator<?> comparator) {
    check().that(actualList).isInOrder(comparator);
  }

  /**
   * @deprecated {@code streamA.isEqualTo(streamB)} always fails, except when passed the exact same
   *     stream reference
   */
  @Override
  @DoNotCall(
      "StreamSubject.isEqualTo() is not supported because Streams do not have well-defined"
          + " equality semantics")
  @Deprecated
  public void isEqualTo(@Nullable Object expected) {
    throw new UnsupportedOperationException(
        "StreamSubject.isEqualTo() is not supported because Streams do not have well-defined"
            + " equality semantics");
  }

  /**
   * @deprecated {@code streamA.isNotEqualTo(streamB)} always passes, except when passed the exact
   *     same stream reference
   */
  @Override
  @DoNotCall(
      "StreamSubject.isNotEqualTo() is not supported because Streams do not have well-defined"
          + " equality semantics")
  @Deprecated
  public void isNotEqualTo(@Nullable Object unexpected) {
    throw new UnsupportedOperationException(
        "StreamSubject.isNotEqualTo() is not supported because Streams do not have well-defined"
            + " equality semantics");
  }

  // TODO(user): Do we want to support comparingElementsUsing() on StreamSubject?
}
