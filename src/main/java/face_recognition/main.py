import cv2
import matplotlib.pyplot as plt
import numpy as np
from imutils.video import VideoStream
from sklearn.decomposition import PCA
from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPClassifier

from dataNineSet import fetch_dataNineSet


def imageRec(dataset, imageToTest):
    lfw_dataset = dataset

    lfw_dataset.data = np.nan_to_num(lfw_dataset.data, nan=0)
    lfw_dataset.images = np.nan_to_num(lfw_dataset.images, nan=0)
    lfw_dataset.target = np.nan_to_num(lfw_dataset.target, nan=0)
    lfw_dataset.target_names = np.nan_to_num(lfw_dataset.target_names, nan=0)

    X = lfw_dataset.data
    y = lfw_dataset.target
    target_names = lfw_dataset.target_names

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

    n_components = 150
    pca = PCA(n_components=n_components, svd_solver='randomized', whiten=True).fit(X_train)

    X_train_pca = pca.transform(X_train)

    clf = MLPClassifier(hidden_layer_sizes=(1024,), batch_size=256, verbose=True, early_stopping=True).fit(X_train_pca,
                                                                                                           y_train)

    imput = np.asarray(imageToTest, dtype=np.float32)
    imput /= 255.0

    imSet = []
    for n in range(107):
        imSet.append(imput)
    imSetOpt = np.asarray(imSet, dtype=np.float32).reshape(len(imSet), -1)

    y_pred = clf.predict(pca.transform(imSetOpt))

    def out(y_pred, target_names, i):
        pred_name = target_names[y_pred[i]].rsplit(' ', 1)[-1]
        return pred_name

    return out(y_pred, target_names, 0)


def loadImgFromWebcam():
    detector = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
    vs = VideoStream(src=0).start()
    while True:
        frame = vs.read()
        orig = frame.copy()
        gray = cv2.cvtColor(orig, cv2.COLOR_BGR2GRAY)
        rects = detector.detectMultiScale(
            gray,
            scaleFactor=1.3,
            minNeighbors=3,
            minSize=(30, 30),
        )
        for (x, y, w, h) in rects:
            cv2.rectangle(orig, (x, y), (x + w, y + h), (0, 255, 0), 2)
            roi_color = orig[y:y + h, x:x + w]
            output = cv2.cvtColor(roi_color, cv2.COLOR_RGB2GRAY)
            output = cv2.resize(output, (250, 250))
            cv2.destroyAllWindows()
            vs.stop()
            return output


def loadImgFromPc(image):
    detector = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    rects = detector.detectMultiScale(
        gray,
        scaleFactor=1.3,
        minNeighbors=3,
        minSize=(30, 30),
    )
    for (x, y, w, h) in rects:
        cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 2)
        roi_color = image[y:y + h, x:x + w]
        output = cv2.cvtColor(roi_color, cv2.COLOR_RGB2GRAY)
        output = cv2.resize(output, (250, 250))
        cv2.destroyAllWindows()
        return output


def main():
    lfw_dataset = fetch_dataNineSet("dataset", min_faces_per_person=40)
    image = loadImgFromWebcam()
    plt.imshow(image, "gray")
    plt.title("Image Taken")
    plt.show()
    out = imageRec(lfw_dataset, image)
    print(out)
    return out


if __name__ == "__main__":
    main()
