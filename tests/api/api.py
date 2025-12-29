from fastapi import FastAPI

def evaluate_safe_ph(ph):
    try:
        ph_value = float(ph)
        return 6.5 <= ph_value <= 7.5
    except ValueError:
        return False

app = FastAPI()
@app.get("/is_ph_safe/")
async def check_ph_safety(ph: float):
    """Endpoint sprawdzający, czy podane pH jest bezpieczne."""
    is_safe = evaluate_safe_ph(ph)
    return {"ph": ph, "is_safe": is_safe, "range": "6.5 - 7.5"}