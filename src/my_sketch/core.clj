(ns my-sketch.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; TODO
; * ボールが底についたらゲームオーバー
; * スタートボタンでゲーム開始
; * ブロックにボールが当たったらブロックを消す
; * ブロックが全て消えたらゲームクリア

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  {:boll-x (/ (q/width) 2)
   :boll-y (/ (q/height) 4)
   :boll-dx +
   :boll-dy +
   :pad-x (/ (q/width) 2)
   :blocks (repeat 5 true)})

(defn update-state [state]
  (with-local-vars [a-state state]
    (var-set a-state (update @a-state :boll-x (:boll-dx @a-state) 5))
    (var-set a-state (update @a-state :boll-y (:boll-dy @a-state) 5))
    (when (> (:boll-x @a-state) (q/width))
      (var-set a-state (assoc @a-state :boll-dx -)))
    (when (< (:boll-x @a-state) 0)
      (var-set a-state (assoc @a-state :boll-dx +)))
    (when (> (:boll-y @a-state) (q/height))
      (var-set a-state (assoc @a-state :boll-dy -)))
    (when (< (:boll-y @a-state) 0)
      (var-set a-state (assoc @a-state :boll-dy +)))
    (when (and (> (:boll-x @a-state) (:pad-x @a-state))
               (< (+ (:boll-x @a-state) 16) (+ (:pad-x @a-state) 60))
               (> (+ (:boll-y @a-state) 16) (- (q/height) 30)))
      (var-set a-state (assoc @a-state :boll-dy -)))
    @a-state))

(defn draw-state [state]
  (q/background 50)
  (q/fill 255)
  (let [bx (:boll-x state)
        by (:boll-y state)
        px (:pad-x state)
        py (- (q/height) 30)]
    ; show boll
    (q/ellipse bx by 16 16)
    ; show pad
    (q/rect px py 60 10)
    ; show blocks
    (doseq [[index show-block] (map-indexed vector (:blocks state))
            :let [x (+ (* 60 index) 110)]]
      (when (true? show-block)
        (q/rect x 100 50 20)))))

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
  :title "breakout"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :key-pressed key-pressed
  :features [:keep-on-top]
  :middleware [m/fun-mode])
