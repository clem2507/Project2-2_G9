from os import listdir, makedirs, remove
from os.path import dirname, join, exists, isdir

import cv2
import numpy as np
from cv2 import imread
from joblib import Memory
from sklearn.datasets._lfw import _load_imgs
from sklearn.utils import Bunch


def getImages(data_folder_path, min_faces_per_person):
    person_names, file_paths = [], []
    for person_name in sorted(listdir(data_folder_path)):
        if person_name == "joblib":
            continue
        folder_path = join(data_folder_path, person_name)
        if not isdir(folder_path):
            continue
        paths = [join(folder_path, f) for f in sorted(listdir(folder_path))]
        n_pictures = len(paths)
        if n_pictures >= min_faces_per_person:
            person_name = person_name.replace('_', ' ')
            person_names.extend([person_name] * n_pictures)
            file_paths.extend(paths)

    n_faces = len(file_paths)
    if n_faces == 0:
        raise ValueError("min_faces_per_person=%d is too restrictive" %
                         min_faces_per_person)

    target_names = np.unique(person_names)
    target = np.searchsorted(target_names, person_names)

    faces = _load_imgs(file_paths)

    indices = np.arange(n_faces)
    np.random.RandomState(42).shuffle(indices)
    faces, target = faces[indices], target[indices]
    return faces, target, target_names


def _load_imgs(file_paths):
    slice_ = (slice(0, 250), slice(0, 250))

    h_slice, w_slice = slice_
    h = (h_slice.stop - h_slice.start) // (h_slice.step or 1)
    w = (w_slice.stop - w_slice.start) // (w_slice.step or 1)

    n_faces = len(file_paths)
    faces = np.zeros((n_faces, h, w), dtype=np.float32)

    for i, file_path in enumerate(file_paths):
        img = imread(file_path, cv2.IMREAD_GRAYSCALE)

        face = np.asarray(img, dtype=np.float32)
        face /= 255.0

        faces[i, ...] = face

    return faces


def fetch_dataNineSet(data_folder_path, min_faces_per_person):
    m = Memory(location="dataset", compress=6, verbose=0)
    load_func = m.cache(getImages)
    faces, target, target_names = load_func(
        data_folder_path, min_faces_per_person=min_faces_per_person)

    X = faces.reshape(len(faces), -1)

    return Bunch(data=X, images=faces, target=target, target_names=target_names)
