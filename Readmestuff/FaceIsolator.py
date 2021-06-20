import os

import cv2

imagePath = "DUMP/DENNIS"
imagePathOut = "DUMP/DENNIS/out/"

counter = 0

for img in os.listdir(imagePath):
    print(img)
    image = cv2.imread(imagePath + "/" + img)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
    faces = faceCascade.detectMultiScale(gray, scaleFactor=1.3, minNeighbors=3, minSize=(30, 30), )

    for (x, y, w, h) in faces:
        cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 2)
        roi_color = image[y:y + h, x:x + w]
        output = cv2.cvtColor(roi_color, cv2.COLOR_RGB2GRAY)
        output = cv2.resize(output, (250, 250))
        cv2.imwrite(imagePathOut + str(counter) + '_faces.jpg', output)
        counter = counter + 1
