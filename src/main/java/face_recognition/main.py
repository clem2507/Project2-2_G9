import os

import cv2
import imutils
import matplotlib.pyplot as plt
import numpy as np
from imutils.video import VideoStream
from sklearn.decomposition import PCA
from sklearn.model_selection import train_test_split, GridSearchCV
# Load data
from sklearn.svm import SVC

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

    # split into a training and testing set
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

    # Compute a PCA
    n_components = 150
    pca = PCA(n_components=n_components, svd_solver='randomized', whiten=True).fit(X_train)

    # apply PCA transformation
    X_train_pca = pca.transform(X_train)

    # train a neural network
    print("Fitting the classifier to the training set")
    param_grid = {'C': [1e3, 5e3, 1e4, 5e4, 1e5],
                  'gamma': [0.0001, 0.0005, 0.001, 0.005, 0.01, 0.1], }
    clf = GridSearchCV(
        SVC(kernel='rbf', class_weight='balanced'), param_grid
    )
    clf = clf.fit(X_train_pca, y_train)

    tIm = np.asarray(imageToTest, dtype=np.float32)
    tIm /= 255.0

    test = []
    for n in range(107):
        test.append(tIm)
    pesttt = np.asarray(test, dtype=np.float32).reshape(len(test), -1)
    trans = pca.transform(pesttt)
    y_pred = clf.predict(trans)

    def title(y_pred, target_names, i):
        pred_name = target_names[y_pred[i]].rsplit(' ', 1)[-1]
        return pred_name

    return title(y_pred, target_names, 0)


def loadpipi():
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

def loadpipiFromPC(image):
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
    image = loadpipi()
    plt.imshow(image, "gray")
    plt.title("Image Taken")
    plt.show()
    out = imageRec(lfw_dataset, image)
    print(out)
    return out


if __name__ == "__main__":
    main()
