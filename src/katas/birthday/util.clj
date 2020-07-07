(ns katas.birthday.util
  (:import (javax.swing JOptionPane JPasswordField)))

(defn password []
  (let [pwd (JPasswordField.)]
    (when
      (= JOptionPane/OK_OPTION
         (JOptionPane/showConfirmDialog
           nil
           pwd
           "Enter password:"
           JOptionPane/OK_CANCEL_OPTION
           JOptionPane/PLAIN_MESSAGE))
      (String. (.getPassword pwd)))))