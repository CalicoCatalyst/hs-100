# hs-100
Simple Java API for TP-Link HS100 Plugs. Although it should work for all HS plugs, hs-100 is the only type it is verified and confirmed to work with.

### Status
As of April.9.2018 Changes and Updates are being worked on. These may include refactoring of the entire class, addition of utilities such as network scanners, plug name identifiers, and other miscellaneous changes. I am currently recoding my application that uses this API and will be making changes to the API that I feel are warranted for regular ease of use and implementation. 

ETA: May 1 2018

## Getting Started

There is one class central to the HS-100 API. This is the HS100.java class. It represents a physical plug.

### Creating a Plug Object

You can create a HS100 Plug with just the IP, and you can additionally specify the Port. 

By default, the port for TP-Link

    HS100 plug = new HS100(String IP);

    // Default local port is almost always 9999!
    HS100 plug = new HS100(String IP, int PORT);
    
### Turning the Plug On and Off

    // On
    plug.switchOn();

    // Off
    plug.switchOff();

### Getting Information

    // Checks if the IP has a device behind it
    plug.isPresent();

    // Checks if the plug is on
    plug.isOn();

    // Checks if the plug is off
    plug.isOff();

    // USER SET INFO //

    // Ip you created the plug with
    plug.getIp();

    // Port assigned to a plug
    plug.getPort();
    
### Setting Information

    // Set the IP
    plug.setIp(String IP);

    // Set the Port
    plug.setPort(Integer PORT);
