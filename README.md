# Mule CoAP Connector
![Mule-Coap logo](icon/icon.svg)

The Mule CoAP Connector is a Mule 4 extension that adds CoAP capability to the  [Mule enterprise service bus](https://www.mulesoft.com/).
Mule 4 applications use this to implement IoT services and/or IoT clients that communicate by means of the CoAP protocol.

The CoAP protocol is specified in [RFC7252 - Constrained Application Protocol](http://tools.ietf.org/html/rfc7252) and other specefications (see below). 
The connector uses Californium, a Java CoAP implementation. More information about Californium and CoAP can be found at:

* [http://www.eclipse.org/californium/](http://www.eclipse.org/californium/)
* [http://coap.technology/](http://coap.technology/).

The complete Mule CoAP Connector documentation can be found at [Teslanet.nl](http://www.teslanet.nl)
## Mule supported versions
* Mule 4.1
* Mule 4.2
* Mule 4.3

## Implemented CoAP specifications
The CoAP protocol is supported as defined in: 
* [IETF rfc 6690](https://tools.ietf.org/html/rfc6690)
* [IETF rfc 7252](https://tools.ietf.org/html/rfc7252)
* [IETF rfc 7641](https://tools.ietf.org/html/rfc7641)
* [IETF rfc 7959](https://tools.ietf.org/html/rfc7959)

## Dependencies
* [Californium](https://www.eclipse.org/californium/) 2.4.1

## Installation

To use Mule CoAP Connector in your Mule 4 application, 
add following dependency to your `pom.xml`.
```xml
  
    <dependency>
        <groupId>nl.teslanet.mule.connectors.coap</groupId>
        <artifactId>mule-coap-connector</artifactId>
        <version>2.0.0-M3</version>
        <classifier>mule-plugin</classifier>
    </dependency>
  
```
After updating dependencies in Anypoint Studio, all CoAP tools (message-sources and message-processors) will be shown in the Anypoint Studio panel - _Mule Palette_.

## Reporting Issues

You can report issues and feature requests at [github](https://github.com/teslanet-nl/mule-coap-connector/issues).

## Contact

Questions or remarks? Create an issue on [github](https://github.com/teslanet-nl/mule-coap-connector/issues).

## Contributing

Use issues or pull-requests on your fork.
