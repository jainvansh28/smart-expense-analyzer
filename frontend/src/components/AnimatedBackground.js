import React, { useEffect, useRef, memo } from 'react';
import './AnimatedBackground.css';

const AnimatedBackground = memo(() => {
  const canvasRef = useRef(null);
  const animationFrameRef = useRef(null);
  const particlesRef = useRef([]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d', { alpha: true });
    
    // Set canvas size
    const resizeCanvas = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };
    resizeCanvas();
    
    // Debounced resize handler
    let resizeTimeout;
    const handleResize = () => {
      clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(resizeCanvas, 150);
    };
    window.addEventListener('resize', handleResize);

    // Particle class for floating currency symbols
    class Particle {
      constructor() {
        this.reset();
        this.y = Math.random() * canvas.height;
        this.symbols = ['₹', '💰', '💵', '💳', '📊', '💎'];
        this.symbol = this.symbols[Math.floor(Math.random() * this.symbols.length)];
      }

      reset() {
        this.x = Math.random() * canvas.width;
        this.y = canvas.height + 50;
        this.speed = 0.3 + Math.random() * 0.4;
        this.opacity = 0.15 + Math.random() * 0.15;
        this.size = 18 + Math.random() * 16;
        this.drift = Math.random() * 1.5 - 0.75;
      }

      update() {
        this.y -= this.speed;
        this.x += this.drift * 0.3;
        
        if (this.y < -50 || this.x < -50 || this.x > canvas.width + 50) {
          this.reset();
        }
      }

      draw() {
        ctx.save();
        ctx.globalAlpha = this.opacity;
        ctx.font = `${this.size}px Arial`;
        ctx.fillStyle = '#a78bfa';
        ctx.fillText(this.symbol, this.x, this.y);
        ctx.restore();
      }
    }

    // Create particles (reduced from 30 to 20 for better performance)
    const particleCount = 20;
    particlesRef.current = [];
    
    for (let i = 0; i < particleCount; i++) {
      particlesRef.current.push(new Particle());
    }

    // Animation loop with throttling
    let lastTime = 0;
    const fps = 30; // Reduced from 60 to 30 for better performance
    const interval = 1000 / fps;
    
    const animate = (currentTime) => {
      animationFrameRef.current = requestAnimationFrame(animate);
      
      const deltaTime = currentTime - lastTime;
      
      if (deltaTime < interval) return;
      
      lastTime = currentTime - (deltaTime % interval);
      
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      
      particlesRef.current.forEach(particle => {
        particle.update();
        particle.draw();
      });
    };

    animationFrameRef.current = requestAnimationFrame(animate);

    return () => {
      window.removeEventListener('resize', handleResize);
      if (animationFrameRef.current) {
        cancelAnimationFrame(animationFrameRef.current);
      }
      clearTimeout(resizeTimeout);
    };
  }, []);

  return (
    <>
      <canvas ref={canvasRef} className="animated-canvas" />
      <div className="gradient-orbs">
        <div className="orb orb-1"></div>
        <div className="orb orb-2"></div>
        <div className="orb orb-3"></div>
      </div>
    </>
  );
});

AnimatedBackground.displayName = 'AnimatedBackground';

export default AnimatedBackground;
