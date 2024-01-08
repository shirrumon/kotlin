declare namespace JS_TESTS {
    type Nullable<T> = T | null | undefined
    namespace foo {
        class Test {
            static bar(): string;
            static readonly foo: string;
            static readonly baz: string;
            static mutable: string;
        }
    }
}
