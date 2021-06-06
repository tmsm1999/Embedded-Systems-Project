//#include <Firebase.h>
//#include <FirebaseArduino.h>
//

#include <ESP8266wifi.h>
#include <WiFi.h>
#include <SoftwareSerial.h>
//#include <FirebaseESP32.h>
#include <ArduinoJson.h>  
#include <PN532.h>
#include <SPI.h>

//#include <<span class="TextRun Highlight BCX0 SCXW174472232" lang="EN-US" xml:lang="EN-US" data-contrast="auto"><span class="SpellingError BCX0 SCXW174472232">FirebaseArduino.h></span></span>
 
// Set these to run example. 
#define FIREBASE_HOST "https://weightingcashierse-default-rtdb.europe-west1.firebasedatabase.app/" 
#define FIREBASE_AUTH "7sP13mBmbaEntwiprBGOHWz1RKVIIdROjXfFvLwD" 
#define WIFI_SSID "preencher"
#define WIFI_PASSWORD "preencher"

#include "HX711.h"
#define DT A1 
#define SCK A0


#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include <Wire.h>
#if defined(__AVR__) || defined(__i386__) //compatibility with Intel Galileo
#define WIRE Wire
#else // Arduino Due
#define WIRE Wire1
#endif

#define IRQ   (2)
#define RESET (3)  // Not connected by default on the NFC Shield

#define PN532_PREAMBLE                      (0x00)
#define PN532_STARTCODE2                    (0xFF)
#define PN532_POSTAMBLE                     (0x00)
#define PN532_HOSTTOPN532                   (0xD4)
#define PN532_PN532TOHOST                   (0xD5)

// PN532 Commands
#define PN532_COMMAND_GETFIRMWAREVERSION    (0x02)
#define PN532_COMMAND_READGPIO              (0x0C)
#define PN532_COMMAND_WRITEGPIO             (0x0E)
#define PN532_COMMAND_SAMCONFIGURATION      (0x14)
#define PN532_COMMAND_RFCONFIGURATION       (0x32)
#define PN532_COMMAND_INLISTPASSIVETARGET   (0x4A)
#define PN532_COMMAND_INDATAEXCHANGE        (0x40)
#define PN532_RESPONSE_INDATAEXCHANGE       (0x41)

#define PN532_I2C_ADDRESS                   (0x48 >> 1)
#define PN532_I2C_BUSY                      (0x00)
#define PN532_I2C_READY                     (0x01)

#define PN532_MIFARE_ISO14443A              (0x00)

// Mifare Commands
#define MIFARE_CMD_AUTH_A                   (0x60)
#define MIFARE_CMD_AUTH_B                   (0x61)
#define MIFARE_CMD_READ                     (0x30)
#define MIFARE_CMD_WRITE                    (0xA0)

// Prefixes for NDEF Records (to identify record type)
#define PN532_GPIO_VALIDATIONBIT            (0x80)
#define PN532_GPIO_P30                      (0)
#define PN532_GPIO_P31                      (1)
#define PN532_GPIO_P32                      (2)
#define PN532_GPIO_P33                      (3)
#define PN532_GPIO_P34                      (4)
#define PN532_GPIO_P35                      (5)


  uint8_t _irq, _reset;
  uint8_t _uid[7];  // ISO14443A uid
  uint8_t _uidLen;  // uid len
  uint8_t _key[6];  // Mifare Classic key
  uint8_t inListedTag; // Tg number of inlisted tag.

  boolean readackframe(void);
  uint8_t wirereadstatus(void);
  void    wirereaddata(uint8_t* buff, uint8_t n);
  void    wiresendcommand(uint8_t* cmd, uint8_t cmdlen);
  boolean waitUntilReady(uint16_t timeout);
  

  byte pn532ack[] = {0x00, 0x00, 0xFF, 0x00, 0xFF, 0x00};
  byte pn532response_firmwarevers[] = {0x00, 0xFF, 0x06, 0xFA, 0xD5, 0x03};

  // Uncomment these lines to enable debug output for PN532(I2C) and/or MIFARE related code
  // #define PN532DEBUG
  // #define MIFAREDEBUG

  #define PN532_PACKBUFFSIZ 64
  byte pn532_packetbuffer[PN532_PACKBUFFSIZ];


byte server[] = { 192, 168, 1, 99 };
WiFiClient client;
int status = WL_IDLE_STATUS;

HX711 escala;

SoftwareSerial mySerial(9,10);


void wifiLink()
{
  
    while(WiFi.status() == WL_NO_SHIELD){
    Serial.print("Wifi Shield not Connected...");
    Serial.println();
    delay(2000);
  } 
  
   Serial.print("Shield Connected!!!");
   Serial.println();


   WiFi.begin(WIFI_SSID,WIFI_PASSWORD);
   Serial.print("Connecting to network...");
   Serial.println();
  
  while(WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(2000);
  }
  
  Serial.print("Connected to Wifi!!!");
  Serial.println();
}

 float getWeight(){
  float peso; 
  Serial.print("Peso: ");
  peso = abs(escala.get_units(20));
  Serial.println(peso);
  Serial.println(" kg"); 

 return peso;
}


static inline void wiresend(uint8_t x) 
{
   #if ARDUINO >= 100
   WIRE.write((uint8_t)x);
   #else
   WIRE.send(x);
   #endif
}

static inline uint8_t wirerecv(void) 
{
   #if ARDUINO >= 100
   return WIRE.read();
   #else
   return WIRE.receive();
   #endif
}

void PrintHex(const byte * data, const uint32_t numBytes)
{
   uint32_t szPos;
   for(szPos=0; szPos < numBytes; szPos++) 
   {
     Serial.print(F("0x"));
     // Append leading 0 for small values
     if (data[szPos] <= 0xF)
     Serial.print(F("0"));
     Serial.print(data[szPos]&0xff, HEX);
     if((numBytes > 1) && (szPos != numBytes - 1))
     {
       Serial.print(F(" "));
     }
   }
     Serial.println();
}

boolean SendCommandCheckAck(uint8_t *cmd, uint8_t cmdlen , uint16_t timeout) 
{
  uint16_t timer = 0;

  
  // write the command
  wiresendcommand(cmd, cmdlen);
  
  // Wait for chip to say its ready!
  while (wirereadstatus() != PN532_I2C_READY) {
    if (timeout != 0) {
      timer+=10;
      if (timer > timeout)  
        return false;
    }
    delay(10);
  }
  
  #ifdef PN532DEBUG
  Serial.println(F("IRQ received"));
  #endif
  
  // read acknowledgement
  if (!readackframe()) {
    #ifdef PN532DEBUG
    Serial.println(F("No ACK frame received!"));
    #endif
    return false;
  }

  return true; // ack'd command
}

boolean  SAMConfig(void) 
{
  pn532_packetbuffer[0] = PN532_COMMAND_SAMCONFIGURATION;
  pn532_packetbuffer[1] = 0x01; // normal mode;
  pn532_packetbuffer[2] = 0x14; // timeout 50ms * 20 = 1 second
  pn532_packetbuffer[3] = 0x01; // use IRQ pin!
  
  if (! SendCommandCheckAck(pn532_packetbuffer, 4,1000))
     return false;

  // read data packet
  wirereaddata(pn532_packetbuffer, 8);
  
  return  (pn532_packetbuffer[6] == 0x15);
}

boolean  readackframe(void) 
{
 uint8_t ackbuff[6];
  
 wirereaddata(ackbuff, 6);
    
 return (0 == strncmp((char *)ackbuff, (char *)pn532ack, 6));
}

uint8_t  wirereadstatus(void) {
  uint8_t x = digitalRead(IRQ);
  
  if (x == 1)
    return PN532_I2C_BUSY;
  else
    return PN532_I2C_READY;
}

void  wirereaddata(uint8_t* buff, uint8_t n) {
  uint16_t timer = 0;
  
  delay(2); 

#ifdef PN532DEBUG
  Serial.print(F("Reading: "));
#endif
  // Start read (n+1 to take into account leading 0x01 with I2C)
  WIRE.requestFrom((uint8_t)PN532_I2C_ADDRESS, (uint8_t)(n+2));
  // Discard the leading 0x01
  wirerecv();
  for (uint8_t i=0; i<n; i++) {
    delay(1);
    buff[i] = wirerecv();
#ifdef PN532DEBUG
    Serial.print(F(" 0x"));
    Serial.print(buff[i], HEX);
#endif
  }
  // Discard trailing 0x00 0x00
  // wirerecv();
    
#ifdef PN532DEBUG
  Serial.println();
#endif
}

void  wiresendcommand(uint8_t* cmd, uint8_t cmdlen) {
  uint8_t checksum;

  cmdlen++;
  
#ifdef PN532DEBUG
  Serial.print(F("\nSending: "));
#endif

  delay(2);     // or whatever the delay is for waking up the board

  // I2C START
  WIRE.beginTransmission(PN532_I2C_ADDRESS);
  checksum = PN532_PREAMBLE + PN532_PREAMBLE + PN532_STARTCODE2;
  wiresend(PN532_PREAMBLE);
  wiresend(PN532_PREAMBLE);
  wiresend(PN532_STARTCODE2);

  wiresend(cmdlen);
  wiresend(~cmdlen + 1);
 
  wiresend(PN532_HOSTTOPN532);
  checksum += PN532_HOSTTOPN532;

#ifdef PN532DEBUG
  Serial.print(F(" 0x")); Serial.print(PN532_PREAMBLE, HEX);
  Serial.print(F(" 0x")); Serial.print(PN532_PREAMBLE, HEX);
  Serial.print(F(" 0x")); Serial.print(PN532_STARTCODE2, HEX);
  Serial.print(F(" 0x")); Serial.print(cmdlen, HEX);
  Serial.print(F(" 0x")); Serial.print(~cmdlen + 1, HEX);
  Serial.print(F(" 0x")); Serial.print(PN532_HOSTTOPN532, HEX);
#endif

  for (uint8_t i=0; i<cmdlen-1; i++) {
   wiresend(cmd[i]);
   checksum += cmd[i];
#ifdef PN532DEBUG
   Serial.print(F(" 0x")); Serial.print(cmd[i], HEX);
#endif
  }
  
  wiresend(~checksum);
  wiresend(PN532_POSTAMBLE);
  
  // I2C STOP
  WIRE.endTransmission();

#ifdef PN532DEBUG
  Serial.print(F(" 0x")); Serial.print(~checksum, HEX);
  Serial.print(F(" 0x")); Serial.print(PN532_POSTAMBLE, HEX);
  Serial.println();
#endif
} 

 String compareTag(const byte * data, const uint32_t numBytes){
  const byte * Banana[numBytes] =   {0xF9,0x74,0xD6,0x01}; 
  const byte * Laranja[numBytes]=   {0x5B,0xE6,0x6C,0x9B};
  const byte * Uva[numBytes]=       {0xF9,0xD8,0xD3,0x01};
  const byte * Manga[numBytes]=     {0x5B,0xE6,0x6C,0x98};
  const byte * Framboesa[numBytes]= {0x2C,0x9E,0xD3,0x01};
  const byte * Amora[numBytes]=     {0x8A,0xDB,0xD3,0x01};
  
                                 



   if ((int)Banana[0] == (int)data[0] && (int)Banana[1] == (int)data[1] && (int)Banana[2] == (int)data[2] && (int)Banana[3] == (int)data[3])
   {
    Serial.println("Bananas!"); 
    return "Banana";   
   }   

   else if((int)Laranja[0] == (int)data[0] && (int)Laranja[1] == (int)data[1] && (int)Laranja[2] == (int)data[2] && (int)Laranja[3] == (int)data[3])
   {
    Serial.println("Laranjas!!!");
    return "Laranja";
   }
   else if((int)Uva[0] == (int)data[0] && (int)Uva[1] == (int)data[1] && (int)Uva[2] == (int)data[2] && (int)Uva[3] == (int)data[3])
   {
    Serial.println("Uvas!!!");
    return("Uva");
   }

   else if((int)Manga[0] == (int)data[0] && (int)Manga[1] == (int)data[1] && (int)Manga[2] == (int)data[2] && (int)Manga[3] == (int)data[3])
   {
    Serial.println("Manga!!!");
    return("Manga");
   }

    else if((int)Framboesa[0] == (int)Framboesa[0] && (int)Framboesa[1] == (int)data[1] && (int)Framboesa[2] == (int)data[2] && (int)Framboesa[3] == (int)data[3])
   {
    Serial.println("Framboesa!!!");
    return("Framboesa");
   }

    else if((int)Amora[0] == (int)Amora[0] && (int)Amora[1] == (int)data[1] && (int)Amora[2] == (int)data[2] && (int)Amora[3] == (int)data[3])
   {
    Serial.println("Amora!!!");
    return("Amora");
   }

  else 
    Serial.println("Produto não reconhecido");  
  }

  boolean  readPassiveTargetID(uint8_t cardbaudrate, uint8_t * uid, uint8_t * uidLength) {
  pn532_packetbuffer[0] = PN532_COMMAND_INLISTPASSIVETARGET;
  pn532_packetbuffer[1] = 1;  // max 1 cards at once (we can set this to 2 later)
  pn532_packetbuffer[2] = cardbaudrate;
  
  if (! SendCommandCheckAck(pn532_packetbuffer, 3,1000))
  {
    #ifdef PN532DEBUG
  Serial.println(F("No card(s) read"));
  #endif
    return 0x0;  // no cards read
  }
  
  // Wait for a card to enter the field
  uint8_t status = PN532_I2C_BUSY;
  #ifdef PN532DEBUG
  Serial.println(F("Waiting for IRQ (indicates card presence)"));
  #endif
  while (wirereadstatus() != PN532_I2C_READY)
  {
    delay(10);
  }

  #ifdef PN532DEBUG
  Serial.println(F("Found a card"));
  #endif
 
  // read data packet
  wirereaddata(pn532_packetbuffer, 20);
  
  // check some basic stuff
  // ISO14443A card response should be in the following format:
  
                                  
  
#ifdef MIFAREDEBUG
    Serial.print(F("Found ")); Serial.print(pn532_packetbuffer[7], DEC); Serial.println(F(" tags"));
#endif
  if (pn532_packetbuffer[7] != 1) 
    return 0;
    
  uint16_t sens_res = pn532_packetbuffer[9];
  sens_res <<= 8;
  sens_res |= pn532_packetbuffer[10];
#ifdef MIFAREDEBUG
    Serial.print(F("ATQA: 0x"));  Serial.println(sens_res, HEX);
    Serial.print(F("SAK: 0x"));  Serial.println(pn532_packetbuffer[11], HEX);
#endif
  
  /* Card appears to be Mifare Classic */
  *uidLength = pn532_packetbuffer[12];
#ifdef MIFAREDEBUG
    Serial.print(F("UID:"));
#endif
  for (uint8_t i=0; i < pn532_packetbuffer[12]; i++) 
  {
    uid[i] = pn532_packetbuffer[13+i];
#ifdef MIFAREDEBUG
      Serial.print(F(" 0x"));Serial.print(uid[i], HEX);
#endif
  }
#ifdef MIFAREDEBUG
    Serial.println();
#endif

  return 1;
}

  


String readCard(){
 uint8_t success;
    uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
    uint8_t uidLength; 
    String produto;// Length of the UID (4 or 7 bytes depending on ISO14443A card type)
    
    // Wait for an ISO14443A type cards (Mifare, etc.).  When one is found
    // 'uid' will be populated with the UID, and uidLength will indicate
    // if the uid is 4 bytes (Mifare Classic) or 7 bytes (Mifare Ultralight)
    success =  readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength); 
    if(success)
    {
      // Display some basic information about the card
    //  Serial.println("Found an ISO14443A card");
    //  Serial.print("ID Length: ");
    //  Serial.print(uidLength, DEC);
    //  Serial.println(" bytes");
     // Serial.print("ID: ");
     // PrintHex(uid, uidLength);
      Serial.println("");
      produto = compareTag(uid, uidLength);    
      }

      return produto;
}


  void putInfo(String artigo, float peso){
    
    String request = "GET /putdata?cashier=1&product=";
    
    if (client.connect(server,8080)) {

    Serial.println("connected to server");

    // Make a HTTP request:
    Serial.println("GET /putdata?cashier=1&product=" + artigo + "&weight=" + peso + " HTTP/1.1");

    client.println("GET /putdata?cashier=1&product=" + artigo + "&weight=" + peso + " HTTP/1.1");

    client.println("Host:192.168.1.99");

    client.println("Connection: close");

    client.println();

  }
}
 
void setup() { 
  mySerial.begin(9600);
  Serial.begin(9600); 
 

// inicialização do Wifi Shield
  wifiLink(); 

// ligação ao firebase
// Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

// inicialização da balança
  escala.begin(DT, SCK);
  Serial.print("Leitura da Tara: ");
  Serial.println(escala.read()); //Aguarda o termino de verificação do peso 
  Serial.println("Aguarde!");
  Serial.println("Iniciando...");
  escala.set_scale(2855040.81);
  escala.tare(20);

// inicialização do Nfc Shield
 uint32_t PN5xxversion; 
    pinMode(IRQ, INPUT);
    pinMode(RESET, OUTPUT);
    Serial.begin(115200);
    //Serial.println("Nfc Shield inicializado");
    WIRE.begin();

    // Reset the PN532  
    digitalWrite(RESET, HIGH);
    digitalWrite(RESET, LOW);
    delay(500);
    digitalWrite(RESET, HIGH);
    SAMConfig();
   
} 
 
//int n = 0; 
 
 void loop() { 
 String artigo;
 float peso;
  Serial.print("Coloque a tag");
  Serial.println();
  artigo = readCard();
  Serial.println("Coloque o artigo na balança");
  delay(8000);
  peso = getWeight();
  putInfo(artigo,peso);

  while (client.available()) {

    char c = client.read();
    Serial.write(c);

  }

  // if the server's disconnected, stop the client:

  if (!client.connected()) {

    Serial.println();

    Serial.println("disconnecting from server.");

    client.stop();

    // do nothing forevermore:

    while (true);

  }
  delay(8000);
  
  
 }
