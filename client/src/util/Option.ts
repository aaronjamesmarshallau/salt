export abstract class Option<T> {
  abstract get(): T;
  abstract getOrElse(orElse: T): T;
  abstract map<A>(fn: (input: T) => A): Option<A>;
  abstract flatMap<A>(fn: (input: T) => Option<A>): Option<A>;
}

class Some<T> extends Option<T> {
  constructor(private value: T) {
    super();
  }
  get(): T {
    return this.value;
  }
  getOrElse(_orElse: T): T {
    return this.value;
  }
  map<A>(fn: (input: T) => A): Option<A> {
    return some(fn(this.value));
  }
  flatMap<A>(fn: (input: T) => Option<A>): Option<A> {
    return fn(this.value);
  }
}

class None<T> extends Option<T> {
  get(): T {
    throw new Error("get called on None.");
  }
  getOrElse(orElse: T): T {
    return orElse;
  }
  map<A>(_fn: (input: T) => A): Option<A> {
    return none<A>();
  }
  flatMap<A>(_fn: (input: T) => Option<A>): Option<A> {
    return none<A>();
  }
}

export const some = <T,>(value: T): Option<T> => {
  return new Some(value);
};

export const none = <T,>(): Option<T> => {
  return new None();
};
