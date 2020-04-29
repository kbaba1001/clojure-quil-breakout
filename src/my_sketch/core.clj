(ns my-sketch.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; TODO
; * ボールをパッドで反射
; * ボールが底についたらゲームオーバー
; * スタートボタンでゲーム開始
; * ブロックを表示
; * ブロックにボールが当たったらブロックを消す
; * ブロックが全て消えたらゲームクリア

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :rgb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:boll-x (/ (q/width) 2)
   :boll-y (/ (q/height) 4)
   :boll-dx +
   :boll-dy +
   :pad-x (/ (q/width) 2)})

(defn update-state [state]
  (with-local-vars [a-state state]
    (var-set a-state (update @a-state :boll-x (:boll-dx @a-state) 10))
    (var-set a-state (update @a-state :boll-y (:boll-dy @a-state) 10))
    (when (> (:boll-x @a-state) (q/width))
      (var-set a-state (assoc @a-state :boll-dx -)))
    (when (< (:boll-x @a-state) 0)
      (var-set a-state (assoc @a-state :boll-dx +)))
    (when (> (:boll-y @a-state) (q/height))
      (var-set a-state (assoc @a-state :boll-dy -)))
    (when (< (:boll-y @a-state) 0)
      (var-set a-state (assoc @a-state :boll-dy +)))
    @a-state))

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 50)
  ; Set circle color.
  (q/fill 255)
  ; Calculate x and y coordinates of the circle.
  (let [bx (:boll-x state)
        by (:boll-y state)
        px (:pad-x state)
        py (- (q/height) 30)]
    (q/ellipse bx by 16 16)
    (q/rect px py 60 10)))
  ; (let [angle (:angle state)
  ;       x (* 150 (q/cos angle))
  ;       y (* 150 (q/sin angle))]
  ;   ; Draw the circle.
  ;   (q/ellipse x y 16 16)))


(defn key-pressed [state event]
  (condp = (:key event)
    :left (if (> (:pad-x state) 0)
            (update state :pad-x - 10)
            state)
    :right (if (< (+ (:pad-x state) 60) (q/width))
             (update state :pad-x + 10)
             state)
    state))

(q/defsketch my-sketch
  :title "You spin my circle right round"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :key-pressed key-pressed
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
