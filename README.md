# Interlayer / Structure.Elementary / Java

This is a Java version of [Structure.Elementary][docs] project. Please
follow the link to understand its purpose.

This repository is in `alpha` maturity stage.

## Contents

At the moment of writing `core`, `manipulation` and `translation` 
components were implemented.

## Coordinates

During the alpha stage, projects coordinates are:

```
space.interlayer.structure.elementary.alpha:core:<version>
space.interlayer.structure.elementary.alpha:translation:<version>
space.interlayer.structure.elementary.alpha:manipulation:<version>
```

## Implementations

None known at this moment.

## Notes

`Node<N>` interface is generic, so it's children or implementations 
would be able to provide more concrete type for basic methods to avoid 
redundant casting (e.g. thanks to that `.asKeyValueNode()` returns a 
structure of `N`, not just `Node`, and extracted nested nodes would
retain their type). If particular consumer doesn't care about specific
type, it could use just `Node<?>` for its processing.

Also, some wildcards may seem strange at first sight:

```java
interface KeyValueNode<N> {
  Map<? extends N, ? extends N> toMap();  
}
```

This is done because if it would use just `N`, child interfaces would 
not be able to overload such method with other (usually, more concrete) 
type. 

## Licensing

interlayer.space community, 2021

MIT & UPL-1.0

  [docs]: https://github.com/interlayer-space/structure.elementary
