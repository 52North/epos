# 52°North EPOS

EPOS provides event stream processing components using a powerful and
extensible encoding/decoding architecture.

## Introduction

The [52°North Sensor Event Service](https://github.com/52North/SES)
established the use of event streams
for complex event processing. While the architecture showed its
potential, additional use cases appeared where the specific SES
interface was a significant overhead. This project narrows the event
pattern components developed within the SES down to its core
functionality and allows its inclusion into other software. In
particular, it can help to enable Publish/Subscribe functionality for
data provisioning web services.

The current development snapshot of the SES uses this components and
will include it in prior releases, starting with version 1.3.0.

## Stream Processing

The stream processing and pattern matching features are heavily based on 
[Esper](http://esper.codehaus.org/).

## Extending EPOS

EPOS provides two interfaces which allow easy extension of the framework

 1 org.n52.epos.transform.EposTransformer
 1 org.n52.epos.filter.FilterInstantiationRepository
 
The `EposTransformer` interface provides methods for transformation of
every kind of input into an EposEvent instance. A realization shall
determine at runtime if it supports the given input by returning a
boolean for  the ` supportsInput(Object input)` method. If true, the
`EposEvent transform(Object input)` is called in which the realization
processes the given object.

The `FilterInstantiationRepository` can be used to implement additional
filters. There are two basic types of filters which are then combined as
a `Rule`:

 * org.n52.epos.filter.ActiveFilter
 * org.n52.epos.filter.PassiveFilter

A realization of `FilterInstantiationRepository` can be considered as a
transformer of a Filter representation (e.g. an XPath configuration)
into the internal `EposFilter` instance. An example is the
`org.n52.epos.engine.filter.XPathFilterRepository`. 

## Building

`mvn clean install` does the basic job. This includes integration-level
tests to ensure the stable behavior of the stream processing.

## Naming

EPOS has no special meaning or is an abbreviation. If desperately
required, one could use "Event Processing Open-closed Suite" as the
complete project name.
